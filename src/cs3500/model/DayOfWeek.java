package cs3500.model;

/**
 * Enum class used to keep track of which day of the week an event is scheduled.
 */
public enum DayOfWeek {

  SUNDAY(1),
  MONDAY(2),
  TUESDAY(3),
  WEDNESDAY(4),
  THURSDAY(5),
  FRIDAY(6),
  SATURDAY(7);

  private final int value;

  DayOfWeek(int value) {
    this.value = value;
  }

  /**
   * Gets the value associated with the day of the week.
   *
   * @return the integer value of the day
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the DayOfWeek associated with the integer.
   *
   * @param dayOfWeek the integer of the day
   * @return the DayOfWeek type associated with the value
   * @throws IllegalArgumentException if the integer doesn't match any day
   */
  public static DayOfWeek dayOf(int dayOfWeek) throws IllegalArgumentException {
    for (DayOfWeek day : DayOfWeek.values()) {
      if (day.getValue() == dayOfWeek) {
        return day;
      }
    }
    throw new IllegalArgumentException("Invalid day of the week");
  }

  /**
   * Returns the day of the week formatted correctly.
   *
   * @return the day of the week with the first letter capitalized and the rest lowercase
   */
  @Override
  public String toString() {
    String name = this.name();
    return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
  }
}
