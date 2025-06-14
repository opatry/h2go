#!/usr/bin/env bash

set -euo pipefail

origin=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd) || exit

cd "${origin}"
./decrypt_file.sh "${origin}/api-9064832520773894557-417132-bdc7875bde18.json.gpg"
./decrypt_file.sh "${origin}/h2go.keystore.gpg"
