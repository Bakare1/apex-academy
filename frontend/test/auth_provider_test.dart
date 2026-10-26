import 'package:flutter_test/flutter_test.dart';
import 'package:frontend/providers/auth_provider.dart'; // Adjust import based on your package name in pubspec.yaml

void main() {
  group('AuthProvider Tests', () {
    test('Initial state is unauthenticated and not loading', () {
      final authProvider = AuthProvider();

      expect(authProvider.isAuthenticated, false);
      expect(authProvider.isLoading, false);
      expect(authProvider.token, null);
      expect(authProvider.errorMessage, null);
    });
  });
}