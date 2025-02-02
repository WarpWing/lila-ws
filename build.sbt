inThisBuild(
  Seq(
    scalaVersion      := "3.4.1",
    versionScheme     := Some("early-semver"),
    version           := "3.2",
    semanticdbEnabled := true // for scalafix
  )
)

val os    = if (sys.props.get("os.name").exists(_.startsWith("Mac"))) "osx" else "linux"
val arch  = if (sys.props.get("os.arch").exists(_.startsWith("aarch64"))) "aarch-64" else "x86-64"
val arch_ = arch.replace("-", "_")

val pekkoVersion = "1.0.2"
val kamonVersion = "2.7.1"
val nettyVersion = "4.1.108.Final"
val chessVersion = "16.0.3"

lazy val `lila-ws` = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name         := "lila-ws",
    organization := "org.lichess",
    resolvers ++= Resolver.sonatypeOssRepos("snapshots"),
    resolvers += "lila-maven".at("https://raw.githubusercontent.com/ornicar/lila-maven/master"),
    libraryDependencies ++= Seq(
      ("org.reactivemongo" %% "reactivemongo" % "1.1.0-RC12")
        .exclude("org.scala-lang.modules", "scala-java8-compat_2.13"),
      "org.reactivemongo" % s"reactivemongo-shaded-native-$os-$arch" % "1.1.0-RC12",
      "io.lettuce"        % "lettuce-core"                           % "6.3.2.RELEASE",
      "io.netty"          % "netty-handler"                          % nettyVersion,
      "io.netty"          % "netty-codec-http"                       % nettyVersion,
      ("io.netty"         % s"netty-transport-native-epoll"          % nettyVersion)
        .classifier(s"linux-$arch_"),
      ("io.netty" % s"netty-transport-native-kqueue" % nettyVersion)
        .classifier(s"osx-$arch_"),
      "org.lichess"                %% "scalalib-lila"        % "11.1.7",
      "org.lichess"                %% "scalachess"           % chessVersion,
      "org.lichess"                %% "scalachess-play-json" % chessVersion,
      "org.apache.pekko"           %% "pekko-actor-typed"    % pekkoVersion,
      "com.typesafe.scala-logging" %% "scala-logging"        % "3.9.5",
      "com.github.blemale"         %% "scaffeine"            % "5.2.1"     % "compile",
      "ch.qos.logback"              % "logback-classic"      % "1.5.6",
      "org.playframework"          %% "play-json"            % "3.0.2",
      "io.kamon"                   %% "kamon-core"           % kamonVersion,
      "io.kamon"                   %% "kamon-influxdb"       % kamonVersion,
      "io.kamon"                   %% "kamon-prometheus"     % kamonVersion,
      "io.kamon"                   %% "kamon-system-metrics" % kamonVersion,
      "com.softwaremill.macwire"   %% "macros"               % "2.5.9"     % "provided",
      "com.roundeights"            %% "hasher"               % "1.3.1",
      "org.scalameta"              %% "munit"                % "1.0.0-M12" % Test
    ),
    scalacOptions := Seq(
      "-encoding",
      "utf-8",
      "-rewrite",
      "-source:future-migration",
      "-indent",
      "-explaintypes",
      "-feature",
      "-language:postfixOps",
      "-Xtarget:21",
      "-Wunused:all"
    ),
    javaOptions ++= Seq("-Xms32m", "-Xmx256m")
  )

addCommandAlias("prepare", "scalafixAll; scalafmtAll")
addCommandAlias(
  "check",
  "; scalafixAll --check ; scalafmtCheckAll"
)
