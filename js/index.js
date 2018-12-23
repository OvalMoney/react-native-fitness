import { NativeModules } from 'react-native';

/**
 * Get native getStep with parsed Dates
 * @param startDate
 * @param endDate
 * @returns {*}
 */
const getSteps = ({ startDate, endDate }) =>
  NativeModules.Fitness.getSteps(parseDate(startDate), parseDate(endDate));

/**
 * Get native getDistance with parsed Dates
 * @param startDate
 * @param endDate
 * @returns {*}
 */
const getDistance = ({ startDate, endDate }) =>
  NativeModules.Fitness.getDistance(parseDate(startDate), parseDate(endDate));

/**
 * Check if valid date and parse it
 * @param date: Date to parse
 */
const parseDate = (date) => {
  if(!date){
    throw Error('Date not valid');
  }
  const parsed = Date.parse(date);

  if(Number.isNaN(parsed)){
    throw Error('Date not valid');
  }
  return parsed;
};

export default {
  ...NativeModules.Fitness,
  getSteps,
  getDistance,
};
