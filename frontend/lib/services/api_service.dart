import 'dart:convert';
import 'package:http/http.dart' as http; // <-- Added 'package:' prefix here
import '../models/course.dart';
import '../models/lesson.dart';
import '../models/api_error.dart';

class ApiService {
  // Use http://10.0.2.2:8080 for Android Emulator, or localhost:8080 for Desktop/Web
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1';

  // Fetch all courses
  Future<List<Course>> fetchCourses() async {
    final response = await http.get(Uri.parse('$baseUrl/courses'));

    if (response.statusCode == 200) {
      List<dynamic> body = jsonDecode(response.body);
      return body.map((json) => Course.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load courses');
    }
  }

  // Fetch lessons for a course (JWT Protected)
  Future<List<Lesson>> fetchCourseLessons(int courseId, String jwtToken) async {
    final response = await http.get(
      Uri.parse('$baseUrl/lessons/course/$courseId'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> body = jsonDecode(response.body);
      return body.map((json) => Lesson.fromJson(json)).toList();
    } else if (response.statusCode == 403) {
      final errorJson = jsonDecode(response.body);
      final apiError = ApiError.fromJson(errorJson);
      throw Exception(apiError.message);
    } else {
      throw Exception('Failed to load lessons');
    }
  }
}