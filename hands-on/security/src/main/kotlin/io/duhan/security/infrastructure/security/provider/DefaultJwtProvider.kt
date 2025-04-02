package io.duhan.security.infrastructure.security.provider

import io.duhan.security.application.port.JwtProvider
import io.duhan.security.domain.TokenClaims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class DefaultJwtProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expiration-seconds:1800}") private val accessTokenExpirationSeconds: Long,
    @Value("\${jwt.refresh-token-expiration-seconds:604800}") private val refreshTokenExpirationSeconds: Long,
) : JwtProvider {
    private val logger = LoggerFactory.getLogger(JwtProvider::class.java)

    private val key: SecretKey by lazy {
        try {
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
        } catch (e: Exception) {
            logger.error("JWT 키 초기화 실패", e)
            throw RuntimeException("JWT 키 초기화 실패", e)
        }
    }

    private val jwtParser: JwtParser by lazy {
        Jwts.parser()
            .verifyWith(key)
            .requireIssuer(ISSUER)
            .build()
    }

    override fun createAccessToken(
        id: Long,
        roles: List<String>,
    ): String =
        createToken(
            subject = id.toString(),
            authorities = roles,
            expirationSeconds = accessTokenExpirationSeconds,
            tokenType = TOKEN_TYPE_ACCESS,
        )

    override fun createRefreshToken(
        id: Long,
        roles: List<String>,
    ): String =
        createToken(
            subject = id.toString(),
            authorities = roles,
            expirationSeconds = refreshTokenExpirationSeconds,
            tokenType = TOKEN_TYPE_REFRESH,
        )

    override fun validateAndExtractClaims(token: String): TokenClaims {
        val invalidTokenClaims =
            TokenClaims(
                id = 0L,
                roles = emptyList(),
                isValid = false,
                tokenType = null,
            )
        try {
            val claims = jwtParser.parseSignedClaims(token).payload

            val id = claims.subject.toLongOrNull() ?: 0L
            val roles = (claims[ROLES_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            val permissions = (claims[PERMISSIONS_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            val tokenType = claims[TOKEN_TYPE_KEY] as? String
            val expirationTime = claims.expiration?.toInstant()

            val isValidType = tokenType == TOKEN_TYPE_ACCESS

            return TokenClaims(
                id = id,
                roles = roles,
                permissions = permissions,
                isValid = isValidType,
                tokenType = tokenType,
                expirationTime = expirationTime,
            )
        } catch (e: SignatureException) {
            logger.error("Invalid JWT signature: ${e.message}")
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: ${e.message}")
        }
        return invalidTokenClaims
    }

    private fun createToken(
        subject: String,
        authorities: List<String>,
        expirationSeconds: Long,
        tokenType: String,
    ): String {
        val now = Instant.now()
        val tokenId = UUID.randomUUID().toString()
        val roles = mutableListOf<String>()
        val permissions = mutableListOf<String>()

        authorities.forEach {
            if (it.startsWith("ROLE_")) {
                roles.add(it)
            } else {
                permissions.add(it)
            }
        }

        return Jwts.builder()
            .subject(subject)
            .claim(ROLES_KEY, roles)
            .claim(PERMISSIONS_KEY, permissions)
            .claim(TOKEN_TYPE_KEY, tokenType)
            .id(tokenId)
            .issuer(ISSUER)
            .issuedAt(Date.from(now))
            .notBefore(Date.from(now))
            .expiration(Date.from(now.plus(expirationSeconds, ChronoUnit.SECONDS)))
            .signWith(key)
            .compact()
    }

    companion object {
        private const val ROLES_KEY = "roles"
        private const val PERMISSIONS_KEY = "permissions"
        private const val TOKEN_TYPE_KEY = "typ"
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
        private const val ISSUER = "sample-security-api"
    }
}
/*
* /**
 * JWT 토큰 생성 및 검증을 담당하는 Provider 클래스
 * 모범 사례를 반영하여 구현
 */
@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-validity-in-seconds:1800}") private val accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refresh-token-validity-in-seconds:604800}") private val refreshTokenValidityInSeconds: Long,
    private val tokenBlacklistService: TokenBlacklistService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(JwtProvider::class.java)
        private const val ROLES_KEY = "roles"
        private const val PERMISSIONS_KEY = "permissions"
        private const val TOKEN_TYPE_KEY = "typ"
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
        private const val ISSUER = "secure-api-service"
    }

    // 키 초기화 (지연 초기화로 애플리케이션 시작 시간 최적화)
    private val key: SecretKey by lazy {
        try {
            // Base64 디코딩하여 키 생성
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
        } catch (e: Exception) {
            logger.error("JWT 키 초기화 실패", e)
            throw RuntimeException("JWT 키 초기화 실패", e)
        }
    }

    // JWT 파서 초기화 (지연 초기화 및 재사용으로 성능 최적화)
    private val jwtParser: JwtParser by lazy {
        Jwts.parser()
            .verifyWith(key)
            .requireIssuer(ISSUER)
            .build()
    }

    /**
 * 사용자 인증 정보를 기반으로 액세스 토큰 생성
 */
    fun createAccessToken(authentication: Authentication): String {
        val roles = mutableListOf<String>()
        val permissions = mutableListOf<String>()

        // 권한 정보를 역할과 퍼미션으로 분리
        for (authority in authentication.authorities) {
            val auth = authority.authority
            if (auth.startsWith("ROLE_")) {
                roles.add(auth)
            } else {
                permissions.add(auth)
            }
        }

        return createToken(
            subject = authentication.name,
            roles = roles,
            permissions = permissions,
            expirationSeconds = accessTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_ACCESS
        )
    }

    /**
 * 사용자 인증 정보를 기반으로 리프레시 토큰 생성
 */
    fun createRefreshToken(authentication: Authentication): String {
        val roles = mutableListOf<String>()
        val permissions = mutableListOf<String>()

        // 권한 정보를 역할과 퍼미션으로 분리
        for (authority in authentication.authorities) {
            val auth = authority.authority
            if (auth.startsWith("ROLE_")) {
                roles.add(auth)
            } else {
                permissions.add(auth)
            }
        }

        return createToken(
            subject = authentication.name,
            roles = roles,
            permissions = permissions,
            expirationSeconds = refreshTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_REFRESH
        )
    }

    /**
 * 사용자 ID와 역할, 권한을 기반으로 액세스 토큰 생성
 */
    fun createAccessToken(userId: String, roles: List<String>, permissions: List<String> = emptyList()): String {
        return createToken(
            subject = userId,
            roles = roles,
            permissions = permissions,
            expirationSeconds = accessTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_ACCESS
        )
    }

    /**
 * 사용자 ID와 역할, 권한을 기반으로 리프레시 토큰 생성
 */
    fun createRefreshToken(userId: String, roles: List<String>, permissions: List<String> = emptyList()): String {
        return createToken(
            subject = userId,
            roles = roles,
            permissions = permissions,
            expirationSeconds = refreshTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_REFRESH
        )
    }

    /**
 * 공통 토큰 생성 로직
 */
    private fun createToken(
        subject: String,
        roles: List<String>,
        permissions: List<String>,
        expirationSeconds: Long,
        tokenType: String
    ): String {
        val now = Instant.now()
        val tokenId = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(subject)
            .claim(ROLES_KEY, roles)
            .claim(PERMISSIONS_KEY, permissions)
            .claim(TOKEN_TYPE_KEY, tokenType)
            .id(tokenId) // JWT ID 설정으로 토큰 고유성 보장
            .issuer(ISSUER)
            .issuedAt(Date.from(now))
            .notBefore(Date.from(now)) // 토큰 활성화 시간 (현재)
            .expiration(Date.from(now.plus(expirationSeconds, ChronoUnit.SECONDS)))
            .signWith(key)
            .compact()
    }

    /**
 * 토큰에서 Authentication 객체 추출
 */
    fun getAuthentication(token: String): Authentication {
        try {
            val claims = parseClaims(token)
            val authorities = mutableListOf<GrantedAuthority>()

            // 역할 추가
            val roles = (claims[ROLES_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            for (role in roles) {
                authorities.add(SimpleGrantedAuthority(role))
            }

            // 권한 추가
            val permissions = (claims[PERMISSIONS_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            for (permission in permissions) {
                authorities.add(SimpleGrantedAuthority(permission))
            }

            val principal: UserDetails = User(
                claims.subject,
                "", // 비밀번호 필드는 비워둠 (토큰 인증에서는 사용하지 않음)
                authorities
            )

            return UsernamePasswordAuthenticationToken(principal, token, authorities)
        } catch (e: Exception) {
            logger.error("JWT 인증 객체 변환 실패", e)
            throw e
        }
    }

    /**
 * 토큰 유효성 검증
 * 토큰의 서명, 만료 여부 및 블랙리스트 등록 여부 확인
 */
    fun validateToken(token: String): Boolean {
        try {
            // 토큰이 블랙리스트에 있는지 확인
            if (tokenBlacklistService.isBlacklisted(token)) {
                logger.warn("블랙리스트에 등록된 토큰 사용 시도")
                return false
            }

            // 토큰 파싱 및 검증
            val claims = jwtParser.parseSignedClaims(token).payload

            // 추가 유효성 검사 (필요에 따라 커스터마이징)
            val tokenType = claims[TOKEN_TYPE_KEY] as? String
            if (TOKEN_TYPE_ACCESS != tokenType) {
                logger.warn("액세스 토큰 타입이 아닌 토큰 사용 시도")
                return false
            }

            return true
        } catch (e: SignatureException) {
            logger.error("유효하지 않은 JWT 서명", e)
            return false
        } catch (e: Exception) {
            logger.error("JWT 검증 중 오류 발생", e)
            return false
        }
    }

    /**
 * 리프레시 토큰 유효성 검증
 */
    fun validateRefreshToken(token: String): Boolean {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                logger.warn("블랙리스트에 등록된 리프레시 토큰 사용 시도")
                return false
            }

            val claims = jwtParser.parseSignedClaims(token).payload

            // 리프레시 토큰 타입 확인
            val tokenType = claims[TOKEN_TYPE_KEY] as? String
            if (TOKEN_TYPE_REFRESH != tokenType) {
                logger.warn("리프레시 토큰 타입이 아닌 토큰 사용 시도")
                return false
            }

            return true
        } catch (e: Exception) {
            logger.error("리프레시 토큰 검증 중 오류 발생", e)
            return false
        }
    }

    /**
 * 토큰 무효화 (로그아웃 등에 사용)
 */
    fun invalidateToken(token: String) {
        try {
            val claims = parseClaims(token)
            val expiration = claims.expiration.toInstant()
            val now = Instant.now()

            // 토큰 만료시간까지 블랙리스트에 등록
            val ttlSeconds = ChronoUnit.SECONDS.between(now, expiration)
            if (ttlSeconds > 0) {
                tokenBlacklistService.addToBlacklist(token, ttlSeconds)
            }
        } catch (e: Exception) {
            logger.error("토큰 무효화 중 오류 발생", e)
            throw e
        }
    }

    /**
 * 토큰에서 Claims 추출
 */
    private fun parseClaims(token: String): Claims {
        return jwtParser.parseSignedClaims(token).payload
    }

    /**
 * 토큰에서 사용자 ID 추출
 */
    fun getUserIdFromToken(token: String): String {
        return parseClaims(token).subject
    }

    /**
 * 토큰에서 역할 목록 추출
 */
    fun getRolesFromToken(token: String): List<String> {
        val claims = parseClaims(token)
        return (claims[ROLES_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
    }

    /**
 * 토큰에서 권한 목록 추출
 */
    fun getPermissionsFromToken(token: String): List<String> {
        val claims = parseClaims(token)
        return (claims[PERMISSIONS_KEY] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
    }

    /**
 * 토큰에서 만료 시간 추출
 */
    fun getExpirationFromToken(token: String): Date {
        return parseClaims(token).expiration
    }

    /**
 * 토큰에서 특정 클레임 추출
 */
    fun getClaimFromToken(token: String, claimName: String): Any? {
        val claims = parseClaims(token)
        return claims[claimName]
    }

    /**
 * 토큰이 리프레시 토큰인지 확인
 */
    fun isRefreshToken(token: String): Boolean {
        val claims = parseClaims(token)
        return TOKEN_TYPE_REFRESH == claims[TOKEN_TYPE_KEY]
    }

    /**
 * 토큰의 만료 시간까지 남은 시간(초)
 */
    fun getTokenRemainingTimeInSeconds(token: String): Long {
        val expiration = getExpirationFromToken(token).toInstant()
        val now = Instant.now()
        return ChronoUnit.SECONDS.between(now, expiration)
    }
}
* */
