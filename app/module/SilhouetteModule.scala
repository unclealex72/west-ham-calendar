package module

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth1._
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.{CookieSecretProvider, CookieSecretSettings}
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2._
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{CookieStateProvider, CookieStateSettings, DummyStateProvider}
import com.mohiva.play.silhouette.impl.providers.openid.YahooProvider
import com.mohiva.play.silhouette.impl.providers.openid.services.PlayOpenIDService
import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.impl.services._
import com.mohiva.play.silhouette.impl.util._
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.openid.OpenIdClient
import play.api.libs.ws.WSClient
import scaldi.Module
import security.Definitions.{AuthenticatorType, Env, UserType}
import security.models.daos._
import security.models.services.{UserService, UserServiceImpl}

import scala.concurrent.ExecutionContext

/**
  * The Guice module which wires all Silhouette dependencies.
  */
class SilhouetteModule extends Module {

  bind[UserService] to injected [UserServiceImpl]
  bind[UserDAO] to injected [UserDAOImpl]
  bind[DelegableAuthInfoDAO[OAuth2Info]] to injected [OAuth2InfoDAO]
  bind[CacheLayer] to injected [PlayCacheLayer]

  bind[IDGenerator] to new SecureRandomIDGenerator()
  bind[PasswordHasher] to new BCryptPasswordHasher
  bind[FingerprintGenerator] to new DefaultFingerprintGenerator(false)
  bind[EventBus] to EventBus()
  bind[Clock] to Clock()

  bind[HTTPLayer] to new PlayHTTPLayer(inject[WSClient])
  bind[Env] to Environment[UserType, AuthenticatorType](
    inject[UserService],
    inject[AuthenticatorService[AuthenticatorType]],
    Seq(),
    inject[EventBus])

  bind[SocialProviderRegistry] to SocialProviderRegistry(Seq(
    inject[FacebookProvider],
    inject[GoogleProvider],
    inject[VKProvider],
    inject[ClefProvider],
    inject[TwitterProvider],
    inject[XingProvider],
    inject[YahooProvider]
  ))

  bind[AuthenticatorService[AuthenticatorType]] to {
    val config = inject[Configuration].underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(config, None, inject[FingerprintGenerator], inject[IDGenerator], inject[Clock])
  }

  bind[AuthInfoRepository] to new DelegableAuthInfoRepository(inject[DelegableAuthInfoDAO[OAuth2Info]])(inject[ExecutionContext])

  bind[AvatarService] to injected[GravatarService]

  bind[OAuth1TokenSecretProvider] to {
    val settings = inject[Configuration].underlying.as[CookieSecretSettings]("silhouette.oauth1TokenSecretProvider")
    new CookieSecretProvider(settings, inject[Clock])
  }

  bind[OAuth2StateProvider] to {
    val settings = inject[Configuration].underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, inject[IDGenerator], inject[Clock])
  }

  bind[CredentialsProvider] to {
    val passwordHasher = inject[PasswordHasher]
    new CredentialsProvider(inject[AuthInfoRepository], passwordHasher, Seq(passwordHasher))
  }

  bind[FacebookProvider] to
    new FacebookProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.facebook"))

  bind[GoogleProvider] to
    new GoogleProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.google"))

  bind[VKProvider] to
    new VKProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.vk"))

  bind[ClefProvider] to
    new ClefProvider(inject[HTTPLayer], new DummyStateProvider, inject[Configuration].underlying.as[OAuth2Settings]("silhouette.clef"))

  bind[TwitterProvider] to {
    val settings = inject[Configuration].underlying.as[OAuth1Settings]("silhouette.twitter")
    new TwitterProvider(inject[HTTPLayer], new PlayOAuth1Service(settings), inject[OAuth1TokenSecretProvider], settings)
  }

  bind[XingProvider] to {
    val settings = inject[Configuration].underlying.as[OAuth1Settings]("silhouette.xing")
    new XingProvider(inject[HTTPLayer], new PlayOAuth1Service(settings), inject[OAuth1TokenSecretProvider], settings)
  }

  bind[YahooProvider] to {
    val settings = inject[Configuration].underlying.as[OpenIDSettings]("silhouette.yahoo")
    new YahooProvider(inject[HTTPLayer], new PlayOpenIDService(inject[OpenIdClient], settings), settings)
  }
}
