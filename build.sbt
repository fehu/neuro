lazy val commonSettings = Seq(
  organization  := "feh.tec",
  scalaVersion  := "2.11.5",
  resolvers += "Fehu's github repo" at "http://fehu.github.io/repo"
)

publishArtifact := false

lazy val root = project.in(file("."))
  .settings(commonSettings: _*)
  .aggregate(neuro, export)

lazy val neuro = project in file("neuro") settings (commonSettings: _*)

lazy val export = project.in(file("export"))
  .settings(commonSettings: _*)
  .dependsOn(neuro)
