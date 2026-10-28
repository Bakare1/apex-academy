class LiveSession {
  final int id;
  final String title;
  final String description;
  final String scheduledTime;
  final String roomUrlOrToken; // This holds your Google Meet link!
  final int courseId;
  final String instructorEmail;

  LiveSession({
    required this.id,
    required this.title,
    required this.description,
    required this.scheduledTime,
    required this.roomUrlOrToken,
    required this.courseId,
    required this.instructorEmail,
  });

  factory LiveSession.fromJson(Map<String, dynamic> json) {
    return LiveSession(
      id: json['id'],
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      scheduledTime: json['scheduledTime'] ?? '',
      roomUrlOrToken: json['roomUrlOrToken'] ?? '',
      courseId: json['courseId'] ?? 0,
      instructorEmail: json['instructorEmail'] ?? '',
    );
  }
}