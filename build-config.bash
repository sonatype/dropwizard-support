# shellcheck disable=SC2034
command_default="rebuild"

# shellcheck disable=SC2155
declare -g mvn_exe="$(which mvn)"
declare -g mvn_options=''

function mvn {
  # shellcheck disable=SC2086
  ${mvn_exe} ${mvn_options} "$@"
}

function command_clean {
  mvn clean "$@"
}

function command_mvn {
  mvn "$@"
}

declare -g rebuild_options=''

function command_rebuild {
  # shellcheck disable=SC2086
  mvn clean install ${rebuild_options} "$@"
}

function command_change_version {
  set +o nounset
  newVersion="$1"
  set -o nounset

  set +o errexit
  shift
  set -o errexit

  if [[ -z "$newVersion" ]]; then
    # shellcheck disable=SC2154
    usage "$command <version>"
  fi

  mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion="$newVersion" "$@"
}

function command_license_check {
  mvn -f "$basedir/pom.xml" -N com.mycila:license-maven-plugin:check "$@"
}

function command_license_format {
  mvn -f "$basedir/pom.xml" -N com.mycila:license-maven-plugin:format "$@"
}

function command_site_build {
  mvn -P!include-private clean install dionysus:build
}

function command_site_publish {
  mvn -P!include-private dionysus:publish
}

function command_site_deploy {
  self site_build && self site_publish
}

function command_site_test {
   (cd src/site/hugo && yarn install && yarn start)
}
