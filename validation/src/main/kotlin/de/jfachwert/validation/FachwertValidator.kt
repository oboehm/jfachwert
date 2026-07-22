package de.jfachwert.validation

import de.jfachwert.Fachwert
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jakarta.validation.executable.ExecutableValidator
import jakarta.validation.metadata.BeanDescriptor

/**
 * Der FachwertValidator implementiert das Validator-Interface von Jakarta
 * und validiert Fachwerte auf Basis des internen Validierungsmechanismus.
 *
 * @author oboehm
 * @since 6.8
 */
class FachwertValidator : Validator {

    override fun <T : Any> validate(obj: T, vararg groups: Class<*>): Set<ConstraintViolation<T>> {
        return emptySet()
    }

    fun validate(fachwert: Fachwert, vararg groups: Class<*>): Set<ConstraintViolation<Fachwert>> {
        if (fachwert.isValid) {
            return emptySet()
        }
        return setOf(FachwertConstraintViolation(fachwert))
    }

    override fun <T : Any> validateProperty(`object`: T, propertyName: String, vararg groups: Class<*>): Set<ConstraintViolation<T>> {
        return emptySet()
    }

    override fun <T : Any> validateValue(beanType: Class<T>, propertyName: String, value: Any?, vararg groups: Class<*>): Set<ConstraintViolation<T>> {
        return emptySet()
    }

    override fun getConstraintsForClass(clazz: Class<*>): BeanDescriptor {
        throw UnsupportedOperationException("getConstraintsForClass noch nicht implementiert")
    }

    override fun <T : Any> unwrap(type: Class<T>): T {
        throw UnsupportedOperationException("unwrap noch nicht implementiert")
    }

    override fun forExecutables(): ExecutableValidator {
        throw UnsupportedOperationException("forExecutables noch nicht implementiert")
    }

}