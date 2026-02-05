export interface ApiResponse<T> {
  data: T;
  message: string;
  succes: boolean;
  timestamp: string;
}
