package monocleintro

import monocle.{SimpleLens, Lenses}
import org.specs2.scalaz.Spec


class LensExample extends Spec {

  @Lenses case class AppConfig(clients: ClientsConfig, switches: Switches)
  @Lenses case class Switches(useFeature1: Boolean, useFeature2: Boolean)
  @Lenses case class ClientsConfig(twitter: TwitterConfig, facebook: FacebookConfig)
  @Lenses case class TwitterConfig(endPoint: EndPointConfig, appId: String, secret: String)
  @Lenses case class FacebookConfig(endPoint: EndPointConfig, accessToken: String)
  @Lenses case class EndPointConfig(protocol: Protocol, host: String, port: Int)
  sealed trait Protocol
  case object Http  extends Protocol
  case object Https extends Protocol

  val config: AppConfig = ???

  // let's update twitter port to 5000
  config.copy(clients = config.clients.copy(
    twitter = config.clients.twitter.copy(
      endPoint = config.clients.twitter.endPoint.copy(
        port = 5000
      )
    )
  ))

  // with Lenses
  import AppConfig._, Switches._, ClientsConfig._, EndPointConfig._
  (clients composeLens twitter composeLens TwitterConfig.endPoint composeLens port).set(config, 5000)

  // and with some syntax sugar
  import monocle.syntax._
  config |-> clients |-> twitter |-> TwitterConfig.endPoint |-> port set 5000

  def toogle(feature: SimpleLens[Switches, Boolean]): AppConfig => AppConfig =
    (switches composeLens feature).modifyF(!_)

  toogle(useFeature1)(config)
  toogle(useFeature2)(config)

  def toogleAllFeatures: AppConfig => AppConfig =
    toogle(useFeature1) compose toogle(useFeature2)

  toogleAllFeatures(config)
}
