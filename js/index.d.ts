export declare enum PermissionKind {
  Steps = 0,
  Distances = 1,
  Calories = 2,
  HeartRate = 3,
  Activity = 4,
  SleepAnalysis = 5,
}

export declare enum PermissionAccess {
  Read = 0,
  Wrtie = 1,
}

export declare type Permission = {
  kind: PermissionKind,
  access: PermissionAccess,
}

/**
 * Check if given permissions are granted or not. 
 * 
 * @param permissions Permission[]
 * @return Promise<boolean>
 */
export declare function isAuthorized(permissions: Permission[]): Promise<boolean>

/**
 * Ask permission and return if user granted or not (Android), while, due to 
 * Apple's privacy model, always true is returned in iOS. 
 * 
 * @param permissions Permission[]
 * @return Promise<boolean>
 */
export declare function requestPermissions(permissions: Permission[]): Promise<boolean>

/**
 * Data interval.
 * 
 * @type string
 */
export declare type Interval = 'days' | 'hour' | 'minute'

export declare interface StepsRequest {
  startDate: string
  endDate?: string
  interval?: Interval
}

export declare interface StepRecord {
  startDate: string
  endDate: string
  quantity: number
}

export declare type StepsResponse = StepRecord[]

/**
 * Fetch steps on a given period of time. 
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request StepsRequest
 * @return Promise<StepsResponse>
 */
export declare function getSteps(request: StepsRequest): Promise<StepsResponse>

export declare interface DistanceRequest {
  startDate: string
  endDate: string
  interval: Interval
}

export declare interface DistanceRecord {
  startDate: string
  endDate: string
  quantity: number
}

export declare type DistanceResponse = DistanceRecord[]

/**
 * Fetch distance in meters on a given period of time.
 *  
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request DistanceRequest
 * @return Promise<DistanceResponse>
 */
export declare function getDistance(request: DistanceRequest): Promise<DistanceResponse>

export declare interface CaloriesRequest {
  startDate: string
  endDate: string
  interval: Interval
}

export declare interface CalorieRecord {
  startDate: string
  endDate: string
  quantity: number
}

export declare type CaloriesResponse = CalorieRecord[]

/**
 * Fetch calories burnt in kilocalories on a given period of time.
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request CaloriesRequest
 * @return Promise<CaloriesResponse>
 */
export declare function getCalories(request: CaloriesRequest): Promise<CaloriesResponse>

export declare interface HeartRateRequest {
  startDate: string
  endDate: string
  interval: Interval
}

export declare interface HeartRateRecord {
  startDate: string
  endDate: string
  quantity: number
}

export declare type HeartRateResponse = HeartRateRecord[]

/**
 * Fetch heart rate bpm on a given period of time.
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request HeartRateRequest
 * @return Promise<HeartRateResponse>
 */
export declare function getHeartRate(request: HeartRateRequest): Promise<HeartRateResponse>

export declare interface SleepAnalysisRequest {
  startDate: string
  endDate: string
}

export declare interface SleepAnalysisRecord {
  startDate: string
  endDate: string
  value: number
  sourceName: string
  sourceId: string
}

export declare type SleepAnalysisResponse = SleepAnalysisRecord[]

/**
 * Fetch sleep analysis data on a given period of time. 
 *
 * It requires an Object with startDate and endDate attributes as string. 
 * If startDate is not provided an error will be thrown.
 * 
 * @param request SleepAnalysisRequest
 * @return Promise<SleepAnalysisResponse>
 */
export declare function getSleepAnalysis(request: SleepAnalysisRequest): Promise<SleepAnalysisResponse>
  
/**
 * Available only on Android. 
 * 
 * Subscribe to all Google Fit activities.
 * 
 * @return Promise<boolean>
 */
export declare function subscribeToActivity(): Promise<boolean>

/**
 * Available only on Android. 
 * 
 * Subscribe only to steps from the Google Fit store.
 * 
 * @return Promise<boolean>
 */
export declare function subscribeToSteps(): Promise<boolean>
