package org.jminix.console.resource;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

@RunWith(JUnit4ClassRunner.class)
public class ValueParserTest {
  ValueParser sut;

  @Before
  public void setUp() {
    sut = new ValueParser();
  }

  @Test
  public void A_java_lang_Long_with_null_is_returned_as_null() {
    String fqdnClazz = Long.class.getCanonicalName();
    assertEquals(null, sut.parse(null, fqdnClazz));
  }

  @Test
  public void A_primitive_long_is_0() {
    assertEquals((long) 0, sut.parse(null, "long"));
  }

  @Test
  public void An_empty_string_of_type_long_is_0() {
    assertEquals((long) 0, sut.parse("", "long"));
  }

  @Test
  public void A_java_lang_Byte_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Byte.class.getCanonicalName()));
  }

  @Test
  public void A_primitve_byte_is_0() {
    assertEquals((byte) 0, sut.parse(null, "byte"));
  }

  @Test
  public void An_empty_string_of_type_byte_is_0() {
    assertEquals((byte) 0, sut.parse("", "byte"));
  }

  @Test
  public void A_java_lang_Short_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Short.class.getCanonicalName()));
  }

  @Test
  public void A_primitive_short_is_0() {
    assertEquals((short) 0, sut.parse(null, "short"));
  }

  @Test
  public void An_empty_string_of_type_short_is_0() {
    assertEquals((short) 0, sut.parse("", "short"));
  }

  @Test
  public void A_java_lang_Integer_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Integer.class.getCanonicalName()));
  }

  @Test
  public void A_primitive_int_is_0() {
    assertEquals(0, sut.parse(null, "int"));
  }

  @Test
  public void An_empty_string_of_type_int_is_0() {
    assertEquals(0, sut.parse("", "int"));
  }

  @Test
  public void A_java_lang_Double_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Double.class.getCanonicalName()));
  }

  @Test
  public void A_primitive_double_is_0() {
    assertEquals((double) 0, sut.parse(null, "double"));
  }

  @Test
  public void An_empty_string_of_type_double_is_0() {
    assertEquals((double) 0, sut.parse("", "double"));
  }

  @Test
  public void A_java_lang_Float_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Float.class.getCanonicalName()));
  }

  @Test
  public void A_primitive_float_is_0() {
    assertEquals((float) 0, sut.parse(null, "float"));
  }

  @Test
  public void An_empty_string_of_type_float_is_0() {
    assertEquals((float) 0, sut.parse("", "float"));
  }

  @Test
  public void A_java_lang_Boolean_with_null_is_returned_as_null() {
    assertEquals(null, sut.parse(null, Boolean.class.getCanonicalName()));
  }

  @Test
  public void A_primitive_boolean_is_false() {
    assertEquals(false, sut.parse(null, "boolean"));
  }

  @Test
  public void An_empty_string_of_type_boolean_is_false() {
    assertEquals(false, sut.parse("", "boolean"));
  }
}
