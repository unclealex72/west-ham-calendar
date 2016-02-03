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

  bind[UserService] toNonLazy injected [UserServiceImpl]
  bind[UserDAO] toNonLazy injected [UserDAOImpl]
  bind[DelegableAuthInfoDAO[OAuth2Info]] toNonLazy injected [OAuth2InfoDAO]
  bind[CacheLayer] toNonLazy injected [PlayCacheLayer]

  bind[IDGenerator] toNonLazy new SecureRandomIDGenerator()
  bind[PasswordHasher] toNonLazy new BCryptPasswordHasher
  bind[FingerprintGenerator] toNonLazy new DefaultFingerprintGenerator(false)
  bind[EventBus] toNonLazy EventBus()
  bind[Clock] toNonLazy Clock()

  bind[HTTPLayer] toNonLazy new PlayHTTPLayer(inject[WSClient])
  bind[Env] toNonLazy Environment[UserType, AuthenticatorType](
    inject[UserService],
    inject[AuthenticatorService[AuthenticatorType]],
    Seq(),
    inject[EventBus])

  bind[SocialProviderRegistry] toNonLazy SocialProviderRegistry(Seq(
    inject[FacebookProvider],
    inject[GoogleProvider],
    inject[VKProvider],
    inject[ClefProvider],
    inject[TwitterProvider],
    inject[XingProvider],
    inject[YahooProvider]
  ))

  bind[AuthenticatorService[AuthenticatorType]] toNonLazy {
    val config = inject[Configuration].underlying.as[CookieAuthenticatorSettings]("silhouette.authenticator")
    new CookieAuthenticatorService(config, None, inject[FingerprintGenerator], inject[IDGenerator], inject[Clock])
  }

  bind[AuthInfoRepository] toNonLazy new DelegableAuthInfoRepository(inject[DelegableAuthInfoDAO[OAuth2Info]])(inject[ExecutionContext])

  bind[AvatarService] toNonLazy injected[GravatarService]

  bind[OAuth1TokenSecretProvider] toNonLazy {
    val settings = inject[Configuration].underlying.as[CookieSecretSettings]("silhouette.oauth1TokenSecretProvider")
    new CookieSecretProvider(settings, inject[Clock])
  }

  bind[OAuth2StateProvider] toNonLazy {
    val settings = inject[Configuration].underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, inject[IDGenerator], inject[Clock])
  }

  bind[CredentialsProvider] toNonLazy {
    val passwordHasher = inject[PasswordHasher]
    new CredentialsProvider(inject[AuthInfoRepository], passwordHasher, Seq(passwordHasher))
  }

  bind[FacebookProvider] toNonLazy
    new FacebookProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.facebook"))

  bind[GoogleProvider] toNonLazy
    new GoogleProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.google"))

  bind[VKProvider] toNonLazy
    new VKProvider(
      inject[HTTPLayer], inject[OAuth2StateProvider], inject[Configuration].underlying.as[OAuth2Settings]("silhouette.vk"))

  bind[ClefProvider] toNonLazy
    new ClefProvider(inject[HTTPLayer], new DummyStateProvider, inject[Configuration].underlying.as[OAuth2Settings]("silhouette.clef"))

  bind[TwitterProvider] toNonLazy {
    val settings = inject[Configuration].underlying.as[OAuth1Settings]("silhouette.twitter")
    new TwitterProvider(inject[HTTPLayer], new PlayOAuth1Service(settings), inject[OAuth1TokenSecretProvider], settings)
  }

  bind[XingProvider] toNonLazy {
    val settings = inject[Configuration].underlying.as[OAuth1Settings]("silhouette.xing")
    new XingProvider(inject[HTTPLayer], new PlayOAuth1Service(settings), inject[OAuth1TokenSecretProvider], settings)
  }

  bind[YahooProvider] toNonLazy {
    val settings = inject[Configuration].underlying.as[OpenIDSettings]("silhouette.yahoo")
    new YahooProvider(inject[HTTPLayer], new PlayOpenIDService(inject[OpenIdClient], settings), settings)
  }
}
