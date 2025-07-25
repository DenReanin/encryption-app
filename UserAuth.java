public class UserAuth {

    public static boolean authenticate(String username, String password) {
        try {
            String storedHash = LocalStorage.getUserPasswordHash(username);
            if (storedHash != null) {
                return SimpleHash.verifyPassword(password, storedHash);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Manejo adecuado de la excepcion
        }
        return false;
    }
}
