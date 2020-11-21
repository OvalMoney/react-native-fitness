const Errors = {
  ErrorHKNotAvailable: -100,
  ErrorMethodNotAvailable: -99,
  ErrorDateNotCorrect: -98,
  ErrorNoEvents: -97,
  ErrorEmptyPermissions: -96,
} as const;

export declare type Errors = typeof Errors[keyof typeof Errors];

const PermissionKinds = {
  Steps: 0,
  Distances: 1,
  Calories: 2,
  HeartRate: 3,
  Activity: 4,
  SleepAnalysis: 5,
} as const;

export declare type PermissionKinds = typeof PermissionKinds[keyof typeof PermissionKinds];

const PermissionAccesses = {
  Read: 0,
  Write: 1,
} as const;

export declare type PermissionAccesses = typeof PermissionAccesses[keyof typeof PermissionAccesses];


export declare type Permission = {
  kind: PermissionKinds,
  access: PermissionAccesses,
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
 * Disconnect from Google Fit. 
 * 
 * @return Promise<boolean>
 */
export declare function disconnect(): Promise<boolean>

/**
 * Log out from Google account. 
 * 
 * @return Promise<boolean>
 */
export declare function logout(): Promise<boolean>

/**
 * Data interval.
 * 
 * @type string
 */
export declare type Interval = 'days' | 'hour' | 'minute'

export declare interface Request {
  startDate: string
  endDate?: string
  interval?: Interval
}

export declare interface StepRecord {
  startDate: string
  endDate: string
  quantity: number
}

/**
 * Fetch steps on a given period of time. 
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request Request
 * @return Promise<StepRecord[]>
 */
export declare function getSteps(request: Request): Promise<StepRecord[]>

export declare interface DistanceRecord {
  startDate: string
  endDate: string
  quantity: number
}

/**
 * Fetch distance in meters on a given period of time.
 *  
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request Request
 * @return Promise<DistanceRecord[]>
 */
export declare function getDistances(request: Request): Promise<DistanceRecord[]>

export declare interface CaloriesRecord {
  startDate: string
  endDate: string
  quantity: number
}

/**
 * Fetch calories burnt in kilocalories on a given period of time.
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request Request
 * @return Promise<CalorieRecord[]>
 */
export declare function getCalories(request: Request): Promise<CaloriesRecord[]>

export declare interface HeartRateRecord {
  startDate: string
  endDate: string
  quantity: number
}

/**
 * Fetch heart rate bpm on a given period of time.
 * 
 * If startDate is not provided an error will be thrown. 
 * If endDate is not provided, the current date will be used.
 * Set interval to decide how detailed the returned data is, set it to hour or minute otherwise it defaults to days.
 * 
 * @param request Request
 * @return Promise<HeartRateRecord[]>
 */
export declare function getHeartRate(request: Request): Promise<HeartRateRecord[]>

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

/**
 * Fetch sleep analysis data on a given period of time. 
 *
 * It requires an Object with startDate and endDate attributes as string. 
 * If startDate is not provided an error will be thrown.
 * 
 * @param request SleepAnalysisRequest
 * @return Promise<SleepAnalysisRecord[]>
 */
export declare function getSleepAnalysis(request: SleepAnalysisRequest): Promise<SleepAnalysisRecord[]>
  
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
