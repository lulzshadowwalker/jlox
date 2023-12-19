public class Token {
    final TokenType type;
    final Object literal;
    final String lexeme;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.literal = literal;
        this.lexeme = lexeme;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
