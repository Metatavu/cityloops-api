package fi.metatavu.cityloops.api.test.functional

import org.junit.Assert.assertEquals
import org.junit.Test

class SanityTestIT: AbstractFunctionalTest() {

  /**
   * TODO: Remove when first test is written
   */
  @Test
  fun sanityTest() {
    TestBuilder().use { testBuilder ->
      assertEquals(true, true)
    }
  }
}