package io.duhan.security.application.port

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
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
    @Value("\${jwt.secret:asdfdasfdas}") private val secretKey: String,
    @Value("\${jwt.access-token-validity-in-seconds:3600}") private val accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refresh-token-validity-in-seconds:604800}") private val refreshTokenValidityInSeconds: Long,
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

    private fun createToken(
        subject: String,
        authorities: String,
        expirationSeconds: Long,
        tokenType: String,
    ): String {
        val now = Instant.now()
        val tokenId = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(subject)
            .claim(AUTHORITIES_KEY, authorities)
            .claim(TOKEN_TYPE_KEY, tokenType)
            .id(tokenId) // JWT ID 설정으로 토큰 고유성 보장
            .issuer(ISSUER)
            .issuedAt(Date.from(now))
            .notBefore(Date.from(now)) // 토큰 활성화 시간 (현재)
            .expiration(Date.from(now.plus(expirationSeconds, ChronoUnit.SECONDS)))
            .signWith(key)
            .compact()
    }

    override fun createAccessToken(): String {
        return "ey.aaa"
    }

    override fun createRefreshToken(): String {
        return "eaz.aaa"
    }

    companion object {
        private const val AUTHORITIES_KEY = "auth"
        private const val TOKEN_TYPE_KEY = "typ"
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
        private const val ISSUER = "secure-api-service"
    }
}

/*
* package com.example.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

/**
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
        private const val AUTHORITIES_KEY = "auth"
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
        return createToken(
            subject = authentication.name,
            authorities = authentication.authorities.joinToString(",") { it.authority },
            expirationSeconds = accessTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_ACCESS
        )
    }

    /**
 * 사용자 인증 정보를 기반으로 리프레시 토큰 생성
 */
    fun createRefreshToken(authentication: Authentication): String {
        return createToken(
            subject = authentication.name,
            authorities = authentication.authorities.joinToString(",") { it.authority },
            expirationSeconds = refreshTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_REFRESH
        )
    }

    /**
 * 사용자 ID와 역할을 기반으로 액세스 토큰 생성
 */
    fun createAccessToken(userId: String, roles: List<String>): String {
        return createToken(
            subject = userId,
            authorities = roles.joinToString(","),
            expirationSeconds = accessTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_ACCESS
        )
    }

    /**
 * 사용자 ID와 역할을 기반으로 리프레시 토큰 생성
 */
    fun createRefreshToken(userId: String, roles: List<String>): String {
        return createToken(
            subject = userId,
            authorities = roles.joinToString(","),
            expirationSeconds = refreshTokenValidityInSeconds,
            tokenType = TOKEN_TYPE_REFRESH
        )
    }

    /**
 * 공통 토큰 생성 로직
 */
    private fun createToken(
        subject: String,
        authorities: String,
        expirationSeconds: Long,
        tokenType: String
    ): String {
        val now = Instant.now()
        val tokenId = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(subject)
            .claim(AUTHORITIES_KEY, authorities)
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

            val authorities = claims[AUTHORITIES_KEY].toString()
                .split(",")
                .filter { it.isNotEmpty() }
                .map { SimpleGrantedAuthority(it) }

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
