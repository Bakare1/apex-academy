class Course {
  final int id; // Changed from Long to int
  final String title;
  final String description;
  final double price;
  final String instructorName;
  final String thumbnailUrl;

  Course({
    required this.id,
    required this.title,
    required this.description,
    required this.price,
    required this.instructorName,
    required this.thumbnailUrl,
  });

  factory Course.fromJson(Map<String, dynamic> json) {
    return Course(
      id: json['id'],
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      price: (json['price'] as num).toDouble(),
      instructorName: json['instructorName'] ?? 'DevForge Instructor',
      thumbnailUrl: json['thumbnailUrl'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'price': price,
      'instructorName': instructorName,
      'thumbnailUrl': thumbnailUrl,
    };
  }
}