import { NativeModules } from "react-native";

/**
 * Get native getStep with parsed Dates
 * @param startDate
 * @param endDate
 * @returns {*}
 */
const getSteps = ({ startDate, endDate, interval = "days" }) =>
  NativeModules.Fitness.getSteps(
    parseDate(startDate),
    parseDate(endDate),
    interval
  );

/**
 * Get native getDistance with parsed Dates
 * @param startDate
 * @param endDate
 * @returns {*}
 */
const getDistance = ({ startDate, endDate, interval = "days" }) =>
  NativeModules.Fitness.getDistance(
    parseDate(startDate),
    parseDate(endDate),
    interval
  );

/**
 * Get native getCalories with parsed Dates
 * @param startDate
 * @param endDate
 * @param interval
 * @returns {*}
 */
const getCalories = ({ startDate, endDate, interval = "days" }) =>
  NativeModules.Fitness.getCalories(
    parseDate(startDate),
    parseDate(endDate),
    interval
  );

/**
 * Get native getHeartRate with parsed Dates
 * @param startDate
 * @param endDate
 * @param interval
 * @returns {*}
 */
const getHeartRate = ({ startDate, endDate, interval = "days" }) =>
  NativeModules.Fitness.getHeartRate(
    parseDate(startDate),
    parseDate(endDate),
    interval
  );

/**
 * Get native getSleepAnalysis with parsed Dates
 * @param startDate
 * @param endDate
 * @returns {*}
 */
const getSleepAnalysis = ({ startDate, endDate }) =>
  NativeModules.Fitness.getSleepAnalysis(
    parseDate(startDate),
    parseDate(endDate)
  );

/**
 * Check if valid date and parse it
 * @param date: Date to parse
 */
const parseDate = date => {
  if (!date) {
    throw Error("Date not valid");
  }
  const parsed = Date.parse(date);

  if (Number.isNaN(parsed)) {
    throw Error("Date not valid");
  }
  return parsed;
};


export default {
  ...NativeModules.Fitness,
  getSteps,
  getDistance,
  getCalories,
  getHeartRate,
  getSleepAnalysis,
};
