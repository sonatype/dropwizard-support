def targetDir = new File(project.build.directory)
ant.mkdir(dir: targetDir)

[ 'src/main/config/wro.properties', 'src/main/config/wro.xml' ].each { filename ->
  def file = new File(basedir, filename)
  ant.copy(file: file, todir: targetDir, filtering: true) {
    filterset {
      project.properties.each { key, value ->
        filter(token: key, value: value)
      }
    }
  }
}