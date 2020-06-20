/*
 * Terre
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.terre.util

import java.lang.NumberFormatException
import kotlin.math.max
import kotlin.math.min

/**
 * Represents a version.
 */
class Version : Comparable<Version> {

  private val backing: IntArray

  /**
   * Gets an int array with all the version values.
   */
  val values: IntArray
    get() = this.backing.clone()

  /**
   * Constructs a new version from the given version string.
   *
   * @throws IllegalArgumentException If the version string is empty or uses an invalid format
   */
  constructor(version: String) {
    check(version.isNotBlank()) { "Version string cannot be blank." }
    val parts = version.split(".")
    this.backing = IntArray(parts.size)
    parts.forEachIndexed { index, s ->
      try {
        this.backing[index] = s.toInt()
      } catch (ex: NumberFormatException) {
        throw IllegalArgumentException("Invalid version string: $version, $s isn't an int.")
      }
    }
  }

  /**
   * Constructs a new version.
   */
  constructor(first: Int) {
    this.backing = IntArray(1)
    this.backing[0] = first
  }

  /**
   * Constructs a new version.
   */
  constructor(first: Int, second: Int) {
    this.backing = IntArray(2)
    this.backing[0] = first
    this.backing[1] = second
  }

  /**
   * Constructs a new version.
   */
  constructor(first: Int, second: Int, third: Int) {
    this.backing = IntArray(3)
    this.backing[0] = first
    this.backing[1] = second
    this.backing[2] = third
  }

  /**
   * Constructs a new version.
   */
  constructor(first: Int, second: Int, third: Int, fourth: Int) {
    this.backing = IntArray(4)
    this.backing[0] = first
    this.backing[1] = second
    this.backing[2] = third
    this.backing[3] = fourth
  }

  /**
   * Constructs a new version.
   */
  constructor(first: Int, second: Int, third: Int, fourth: Int, vararg more: Int) {
    this.backing = IntArray(more.size + 4)
    this.backing[0] = first
    this.backing[1] = second
    this.backing[2] = third
    this.backing[3] = fourth
    more.copyInto(destination = this.backing, destinationOffset = 4)
  }

  /**
   * Constructs a new version.
   */
  constructor(values: IntArray) {
    check(values.isNotEmpty()) { "At least one value must be present in the array." }
    this.backing = values.clone()
  }

  private fun getBacking() = this.backing
  private val toString by lazy { getBacking().joinToString(".") }

  /**
   * Compares this version to the other one.
   */
  override fun compareTo(other: Version): Int {
    val s1 = this.backing.size
    val s2 = other.backing.size

    val common = min(s1, s2)
    for (i in 0 until common) {
      val v = this.backing[i].compareTo(other.backing[i])
      if (v != 0)
        return v
    }
    for (i in common until max(s1, s2)) {
      val c1 = if (s1 > s2) this.backing[i] else 0
      val c2 = if (s2 > s1) other.backing[i] else 0

      val v = c1.compareTo(c2)
      if (v != 0)
        return v
    }
    return 0
  }

  /**
   * Checks whether this version contains the specified version.
   *
   * This will only return true if the versions are equal or
   * if the version is a sub-version, e.g. has a minor version
   * component.
   *
   * E.g. 1.3 contains 1.3, 1.3.1 and 1.3.11, but not 1.4
   */
  operator fun contains(other: Version): Boolean {
    if (this.backing.size == other.backing.size) {
      return this.backing contentEquals other.backing
    }
    if (this.backing.size > other.backing.size)
      return false
    val common = min(this.backing.size, other.backing.size)
    for (i in 0 until common) {
      if (this.backing[i] != other.backing[i])
        return false
    }
    return true
  }

  override fun equals(other: Any?) = other === this || (other is Version && this.compareTo(other) == 0)

  override fun hashCode() = this.backing.contentHashCode()

  override fun toString() = this.toString
}
