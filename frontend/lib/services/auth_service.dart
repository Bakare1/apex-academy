import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/api_error.dart';

class AuthService {
  // Use 10.0.2.2 for Android Emulator, localhost for desktop/web
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1/auth';

  Future<String> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'email': email,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      // Adjust key depending on how your backend returns the token (e.g., 'token' or 'jwt')
      return data['token'] ?? data['jwt'] ?? '';
    } else {
      final errorJson = jsonDecode(response.body);
      final apiError = ApiError.fromJson(errorJson);
      throw Exception(apiError.message);
    }
  }
}