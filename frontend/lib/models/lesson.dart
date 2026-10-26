class Lesson {
  final int id;
  final String title;
  final String content;
  final String videoUrl;
  final bool isFreePreview;
  final int sequenceOrder;

  Lesson({
    required this.id,
    required this.title,
    required this.content,
    required this.videoUrl,
    required this.isFreePreview,
    required this.sequenceOrder,
  });

  factory Lesson.fromJson(Map<String, dynamic> json) {
    return Lesson(
      id: json['id'],
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      videoUrl: json['videoUrl'] ?? '',
      isFreePreview: json['isFreePreview'] ?? false,
      sequenceOrder: json['sequenceOrder'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'content': content,
      'videoUrl': videoUrl,
      'isFreePreview': isFreePreview,
      'sequenceOrder': sequenceOrder,
    };
  }
}