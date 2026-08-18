package com.cyberark.conjur.authentication;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberark.conjur.constant.ConjurConstant;
import com.cyberark.conjur.domain.ConjurConfiguration;
import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;

/**
 * Implementing class to get AccesToken from ApiClient
 * 
 * @author Jaleela.FaizurRahman
 *
 */
public class AccessTokenProviderImpl implements AccessTokenProvider {

	 final Logger LOGGER = LoggerFactory.getLogger(AccessTokenProviderImpl.class);

	/*
	 * Method to get the AccessToken for APIKey Authentication
	 * 
	 * @param ApiClient client
	 * 
	 * @return AccessToken
	 * 
	 * @throws ApiException
	 */
	@Override
	public AccessToken getNewAccessToken(ApiClient client) throws ApiException {
		LOGGER.debug("Start: creating new Access Token");
		try {
			return client.getNewAccessToken();
		} catch (Exception ex) {
			throw new ApiException(ex);
		}
	}

	/*
	 * Method to get the AccessToken for JWT Authentication
	 * 
	 * @param ApiClient client
	 * 
	 * @return AccessToken
	 * 
	 * @throws ApiException
	 */
	@Override
	public AccessToken getJwtAccessToken(ApiClient conjurClient, String jwtTokenPath, String authenticatorId) {

		LOGGER.warn("JWT Access Token retrieval is not implemented yet.");
		return null;
	}

	/**
	 * Authenticates via X.509 client certificate (authn-cert / mTLS).
	 * <p>
	 * Builds a custom {@link OkHttpClient} with the client certificate + private
	 * key, performs a POST to {@code /authn-cert/<serviceId>/<account>[/<workloadId>]/authenticate},
	 * and returns an {@link AccessToken} wrapping the base64-encoded Conjur token.
	 * The caller obtains the final header value via {@link AccessToken#getHeaderValue()},
	 * consistent with the API key authentication path.
	 *
	 * @param config all Conjur configuration parameters, including cert-auth fields
	 * @return AccessToken ready to be used via getHeaderValue()
	 * @throws ApiException on authentication failure
	 */
	@Override
	public AccessToken getCertAccessToken(ConjurConfiguration config) throws ApiException {
		LOGGER.debug("Start: authn-cert certificate-based authentication");
		try {
			X509Certificate clientCert = loadCertificate(config);
			PrivateKey clientKey = loadPrivateKey(config);

			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(null, null);
			keyStore.setKeyEntry("conjur-client", clientKey, new char[0], new X509Certificate[]{clientCert});

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, new char[0]);

			TrustManager[] trustManagers = buildTrustManagers(config);
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), trustManagers, null);

			OkHttpClient httpClient = new OkHttpClient.Builder()
					.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
					.build();

			String url = buildAuthnCertUrl(config);
			LOGGER.debug("Sending authn-cert authentication request");

			Request request = new Request.Builder()
					.url(url)
					.post(RequestBody.create(new byte[0]))
					.header("Accept-Encoding", "base64")
					.build();

			try (Response response = httpClient.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					LOGGER.error("authn-cert authentication failed with HTTP status: {}", response.code());
					throw new ApiException(response.code(), ConjurConstant.AUTHENTICATION_FAILED);
				}
				ResponseBody body = response.body();
				if (body == null) {
					LOGGER.error("authn-cert authentication returned empty response body");
					throw new ApiException(ConjurConstant.AUTHENTICATION_FAILED);
				}
				byte[] tokenBytes = body.bytes();
				try {
					String rawToken = new String(tokenBytes, StandardCharsets.UTF_8).trim();
					LOGGER.debug("authn-cert authentication succeeded");
					return AccessToken.fromEncodedToken(rawToken);
				} finally {
					Arrays.fill(tokenBytes, (byte) 0);
				}
			}
		} catch (ApiException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("authn-cert authentication error: {}", ex.getMessage());
			throw new ApiException(ConjurConstant.AUTHENTICATION_FAILED);
		}
	}

	/**
	 * Constructs the authn-cert endpoint URL.
	 * Format: {@code <applianceUrl>/authn-cert/<serviceId>/<account>[/<urlEncodedAuthnLogin>]/authenticate}
	 * If {@code conjurAuthnLogin} is non-empty, the URL includes the workload ID
	 * (request host-mode). If empty, the URL omits it (SPIFFE mode).
	 */
	private String buildAuthnCertUrl(ConjurConfiguration config) {
		String baseUrl = config.getConjurApplianceUrl();
		String serviceId = config.getConjurAuthnCertServiceId();
		String account = config.getConjurAccount();
		String authnLogin = config.getConjurAuthnLogin();

		StringBuilder url = new StringBuilder(baseUrl)
				.append("/authn-cert/")
				.append(serviceId)
				.append("/")
				.append(account);

		if (StringUtils.isNotEmpty(authnLogin)) {
			url.append("/").append(URLEncoder.encode(authnLogin, StandardCharsets.UTF_8));
		}

		url.append("/authenticate");
		return url.toString();
	}

	/**
	 * Loads the client X.509 certificate from file path or inline PEM content.
	 * File path ({@code conjurClientCertFile}) takes precedence over inline PEM
	 * ({@code conjurClientCert}).
	 * <p>
	 * PEM bytes read from disk are zeroed in a {@code finally} block to narrow
	 * the in-memory exposure window of certificate material.
	 */
	private X509Certificate loadCertificate(ConjurConfiguration config) throws Exception {
		if (StringUtils.isNotEmpty(config.getConjurClientCertFile())) {
			byte[] pemBytes = Files.readAllBytes(Paths.get(config.getConjurClientCertFile()));
			try (PEMParser parser = new PEMParser(
					new InputStreamReader(new ByteArrayInputStream(pemBytes), StandardCharsets.UTF_8))) {
				return parseCertificate(parser);
			} finally {
				Arrays.fill(pemBytes, (byte) 0);
			}
		} else if (StringUtils.isNotEmpty(config.getConjurClientCert())) {
			char[] pemChars = config.getConjurClientCert().toCharArray();
			try (PEMParser parser = new PEMParser(new CharArrayReader(pemChars))) {
				return parseCertificate(parser);
			} finally {
				Arrays.fill(pemChars, '\0');
			}
		} else {
			throw new IllegalArgumentException(ConjurConstant.CERT_AUTH_CLIENT_CERT_REQUIRED);
		}
	}

	/**
	 * Loads the client private key from file path or inline PEM content.
	 * File path ({@code conjurClientKeyFile}) takes precedence over inline PEM
	 * ({@code conjurClientKey}). If loading from file, the file permissions are
	 * validated to be owner-read-only (R1 requirement).
	 * <p>
	 * Raw PEM bytes/chars are zeroed in a {@code finally} block immediately after
	 * parsing to limit the in-memory exposure window of private key material.
	 */
	private PrivateKey loadPrivateKey(ConjurConfiguration config) throws Exception {
		if (StringUtils.isNotEmpty(config.getConjurClientKeyFile())) {
			validateKeyFilePermissions(config.getConjurClientKeyFile());
			byte[] pemBytes = Files.readAllBytes(Paths.get(config.getConjurClientKeyFile()));
			try (PEMParser parser = new PEMParser(
					new InputStreamReader(new ByteArrayInputStream(pemBytes), StandardCharsets.UTF_8))) {
				return extractPrivateKey(parser);
			} finally {
				Arrays.fill(pemBytes, (byte) 0);
			}
		} else if (StringUtils.isNotEmpty(config.getConjurClientKey())) {
			char[] pemChars = config.getConjurClientKey().toCharArray();
			try (PEMParser parser = new PEMParser(new CharArrayReader(pemChars))) {
				return extractPrivateKey(parser);
			} finally {
				Arrays.fill(pemChars, '\0');
			}
		} else {
			throw new IllegalArgumentException(ConjurConstant.CERT_AUTH_CLIENT_KEY_REQUIRED);
		}
	}

	/**
	 * Extracts an {@link X509Certificate} from an already-opened {@link PEMParser}.
	 */
	private X509Certificate parseCertificate(PEMParser parser) throws Exception {
		Object obj = parser.readObject();
		if (obj instanceof X509CertificateHolder holder) {
			return new JcaX509CertificateConverter().getCertificate(holder);
		}
		throw new IllegalArgumentException("Unable to parse client certificate from provided source");
	}

	/**
	 * Extracts a {@link PrivateKey} from an already-opened {@link PEMParser}.
	 * Supports traditional (PKCS#1 / SEC1) and PKCS#8 private key formats.
	 */
	private PrivateKey extractPrivateKey(PEMParser parser) throws Exception {
		Object obj = parser.readObject();
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		if (obj instanceof PEMKeyPair keyPair) {
			return converter.getKeyPair(keyPair).getPrivate();
		}
		if (obj instanceof PrivateKeyInfo keyInfo) {
			return converter.getPrivateKey(keyInfo);
		}
		throw new IllegalArgumentException("Unable to parse private key: unsupported PEM format");
	}

	/**
	 * Validates that the private key file has owner-read-only permissions on POSIX
	 * filesystems (Linux / macOS). Throws {@link SecurityException} if the file is
	 * group- or world-readable/writable. The check is silently skipped on
	 * non-POSIX systems (e.g. Windows).
	 */
	private void validateKeyFilePermissions(String keyFilePath) {
		try {
			Path path = Paths.get(keyFilePath);
			Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
			if (perms.contains(PosixFilePermission.GROUP_READ)
					|| perms.contains(PosixFilePermission.GROUP_WRITE)
					|| perms.contains(PosixFilePermission.OTHERS_READ)
					|| perms.contains(PosixFilePermission.OTHERS_WRITE)) {
				throw new SecurityException(ConjurConstant.CERT_AUTH_KEY_FILE_PERMISSIONS);
			}
		} catch (UnsupportedOperationException e) {
			LOGGER.debug("POSIX file permissions not supported on this OS; skipping key file permission check");
		} catch (SecurityException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.debug("Unable to check key file permissions: {}", e.getMessage());
		}
	}

	/**
	 * Builds the {@link TrustManager} array used to verify Conjur's server
	 * certificate. If {@code conjurSslCertificate} or {@code conjurCertFile} is
	 * set, a custom trust store containing that CA certificate is used; otherwise
	 * the JVM default trust store is used.
	 */
	private TrustManager[] buildTrustManagers(ConjurConfiguration config) throws Exception {
		String sslCertPath = StringUtils.isNotEmpty(config.getConjurSslCertificate())
				? config.getConjurSslCertificate()
				: config.getConjurCertFile();

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		if (StringUtils.isNotEmpty(sslCertPath)) {
			try (FileInputStream fis = new FileInputStream(sslCertPath);
				 PEMParser parser = new PEMParser(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
				Object obj = parser.readObject();
				if (!(obj instanceof X509CertificateHolder holder)) {
					throw new IllegalArgumentException("Unable to parse CA certificate from: " + sslCertPath);
				}
				X509Certificate caCert = new JcaX509CertificateConverter().getCertificate(holder);
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				trustStore.setCertificateEntry("conjur-ca", caCert);
				tmf.init(trustStore);
			}
		} else {
			tmf.init((KeyStore) null);
		}

		return tmf.getTrustManagers();
	}

}
