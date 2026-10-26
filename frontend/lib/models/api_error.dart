class ApiError {
  final String timestamp;
  final int status;
  final String error;
  final String message;
  final String path;

  ApiError({
    required this.timestamp,
    required this.status,
    required this.error,
    required this.message,
    required this.path,
  });

  factory ApiError.fromJson(Map<String, dynamic> json) {
    return ApiError(
      timestamp: json['timestamp'] ?? '',
      status: json['status'] ?? 400,
      error: json['error'] ?? 'Bad Request',
      message: json['message'] ?? 'An unexpected error occurred.',
      path: json['path'] ?? '',
    );
  }
}