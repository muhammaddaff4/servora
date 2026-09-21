package com.example

import com.example.data.ServoraRepository
import com.example.model.AppRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRoleSecuritySeparation_OnlyDeveloperCanModifySecurityFlags() {
        val repo = ServoraRepository()

        // Customer cannot modify security flags
        val customerAttempt = repo.toggleSecurityFeatureFlag("sec_escrow_strict", AppRole.CUSTOMER)
        assertFalse("Customer must NOT be allowed to change security flags", customerAttempt)

        // Professional cannot modify security flags
        val proAttempt = repo.toggleSecurityFeatureFlag("sec_escrow_strict", AppRole.PROFESSIONAL)
        assertFalse("Professional must NOT be allowed to change security flags", proAttempt)

        // Admin cannot modify core security flags (only Developer)
        val adminAttempt = repo.toggleSecurityFeatureFlag("sec_escrow_strict", AppRole.ADMIN)
        assertFalse("Admin must NOT be allowed to modify root security flags", adminAttempt)

        // Developer CAN modify security flags
        val devAttempt = repo.toggleSecurityFeatureFlag("sec_escrow_strict", AppRole.DEVELOPER)
        assertTrue("Developer MUST be allowed to toggle security flags", devAttempt)
    }

    @Test
    fun testPinVerification_EnforcesCorrectCodes() {
        val repo = ServoraRepository()

        // Developer PIN
        assertTrue(repo.verifyRolePin(AppRole.DEVELOPER, "9900"))
        assertFalse(repo.verifyRolePin(AppRole.DEVELOPER, "1234"))
        assertFalse(repo.verifyRolePin(AppRole.DEVELOPER, ""))

        // Admin PIN
        assertTrue(repo.verifyRolePin(AppRole.ADMIN, "1122"))
        assertFalse(repo.verifyRolePin(AppRole.ADMIN, "9900"))

        // Customer / Pro requires no PIN
        assertTrue(repo.verifyRolePin(AppRole.CUSTOMER, ""))
        assertTrue(repo.verifyRolePin(AppRole.PROFESSIONAL, ""))
    }
}

