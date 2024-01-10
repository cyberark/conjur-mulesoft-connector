#!/bin/bash

main() {
  CONJUR_ACCESS_TOKEN=$(curl --header "Accept-Encoding: base64" --data "${CONJUR_AUTHN_API_KEY}" ${CONJUR_CLOUD_URL}/authn/${CONJUR_ACCOUNT}/${HOST_IDENTITY}/authenticate)
  echo "conjur access token: $CONJUR_ACCESS_TOKEN"
  SECRET_VAL=$(curl -H "Authorization: Token token=\"$CONJUR_ACCESS_TOKEN\"" ${CONJUR_CLOUD_URL}/secrets/${CONJUR_ACCOUNT}/variable/${VARIABLE_PATH})
  SECRET_VAL_1=$(curl -H "Authorization: Token token=\"$CONJUR_ACCESS_TOKEN\"" ${CONJUR_CLOUD_URL}/secrets/${CONJUR_ACCOUNT}/variable/${VARIABLE_PATH_1})
  SECRET_VAL_2=$(curl -H "Authorization: Token token=\"$CONJUR_ACCESS_TOKEN\"" ${CONJUR_CLOUD_URL}/secrets/${CONJUR_ACCOUNT}/variable/${VARIABLE_PATH_2})
  echo -e "\n retrieved username value is: $SECRET_VAL"
  echo -e "\n retrieved password value is: $SECRET_VAL_1"
  echo -e "\n retrieved address value is: $SECRET_VAL_2"
}
main "$@"
exit