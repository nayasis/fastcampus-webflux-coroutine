package dev.fastcampus.webflux.coroutine.controller

import dev.fastcampus.webflux.coroutine.service.AdvancedService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kotlinx.coroutines.delay
import kotlinx.coroutines.slf4j.MDCContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.InvalidParameterException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.Continuation
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}

@RestController
class AdvancedController(
    private val service: AdvancedService
) {

    @GetMapping("/test/txid")
    suspend fun testTxId() {
//        withContext(MDCContext()) {
            logger.debug { "hello txid" }
            delay(100)
            service.mdc()
            logger.info { "end txid test" }
//        }
    }

    @PostMapping("/test/txid2")
    fun testNonCoroutineTxid() {
        logger.debug { "hello txid with non-coroutine" }
    }

//    @PutMapping("/test/error")
//    fun testError(@RequestBody @Valid request: ReqErrorTest, error: BindingResult) {
////        throw RuntimeException("hello error")
//
//        if(request.flag == "error") { // webflux 에서는 bindresult 를 지원 안함 (주입시킬 수 없음)
//            error.rejectValue(request::flag.name, "business error")
//            throw BindException(error)
//        }
//
//    }

    @PutMapping("/test/error")
    fun testError(@RequestBody @Valid request: ReqErrorTest) {
        logger.debug { "request: $request" }
//        throw RuntimeException("hello error")

        if(request.flag == "error") {
            throw InvalidParameterException(request, request::flag, "business error")
        }

        logger.info { "success" }

    }

}

class InvalidParameterException(
    request: Any,
    field: KProperty<*>,
    message: String = "",
    code: String = "",
): BindException(
    WebDataBinder(request, request::class.simpleName!!).bindingResult.apply {
        rejectValue(field.name, code, message)
    }
)


data class ReqErrorTest(
    @field:NotEmpty
    @field:Size(min=3, max=10)
    val id: String?,
    @field:Positive(message = "양수만 입력 가능")
    @field:NotNull
    @field:Max(100)
    val age: Int?,
    @field:DateString
    val birthday: String?,
    val flag: String? = null,
)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DateValidator::class])
annotation class DateString(
    val message: String = "not a valid date",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class DateValidator: ConstraintValidator<DateString,String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        // 20240921 -> yyyyMMdd
        val text = value?.filter { it.isDigit() } ?: return true
        return runCatching { LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyyMMdd")) }
            .getOrNull() != null
    }

}




@Aspect
@Component
class AdvancedAspectConfig {

    @Around("""
        @annotation(org.springframework.web.bind.annotation.RequestMapping) ||
        @annotation(org.springframework.web.bind.annotation.GetMapping) ||
        @annotation(org.springframework.web.bind.annotation.PostMapping) ||
        @annotation(org.springframework.web.bind.annotation.PutMapping) ||
        @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
        @annotation(org.springframework.web.bind.annotation.PatchMapping)
    """)
    fun bindMdcContext(jp: ProceedingJoinPoint): Any? {

        logger.debug { """
            >> bind MDC context
            - method: ${jp.signature}
            - args:   ${jp.args.toList()}
        """.trimIndent() }

        return if(jp.hasSuspendFunction) {
            val continuation = jp.args.last() as Continuation<*>
            val newContext = continuation.context + MDCContext()
            val newContinuation = Continuation(newContext) { continuation.resumeWith(it) }
            val newArgs = jp.args.dropLast(1) + newContinuation

            jp.proceed(newArgs.toTypedArray())
        } else {
            jp.proceed()
        }
    }


    private val ProceedingJoinPoint.hasSuspendFunction: Boolean
        get() {
            val method = (this.signature as MethodSignature).method
            return KotlinDetector.isSuspendingFunction(method)
        }

}