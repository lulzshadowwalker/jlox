import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("и",       TokenType.AND);
        keywords.put("класс",   TokenType.CLASS);
        keywords.put("иначе",   TokenType.ELSE);
        keywords.put("ложь",    TokenType.FALSE);
        keywords.put("для",     TokenType.FOR);
        keywords.put("функция", TokenType.FUN);
        keywords.put("если",    TokenType.IF);
        keywords.put("пусто",   TokenType.NIL);
        keywords.put("или",     TokenType.OR);
        keywords.put("вывести", TokenType.PRINT);
        keywords.put("вернуть", TokenType.RETURN);
        keywords.put("супер",   TokenType.SUPER);
        keywords.put("это",     TokenType.THIS);
        keywords.put("правда",  TokenType.TRUE);
        keywords.put("переменная", TokenType.VAR);
        keywords.put("пока",    TokenType.WHILE);

    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = next();

        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;

            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;

            case ' ':
            case '\r':
            case '\t': break;

            case '\n': line++; break;
            case '"': string(); break;

            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) { next(); }
                    break;
                }

                addToken(TokenType.SLASH);
                break;

            default:
                if (isDigit(c)) {
                    number();
                    break;
                }  else if (isAlpha(c)) {
                    identifier();
                    break;
                } else {
                    Lox.error(line, "Unexpected character.");
                    break;
                }
        }
    }

    private boolean isAlpha(char c) {
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'а' && c <= 'я') ||
                (c >= 'А' && c <= 'Я') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    private void identifier() {
        while (isAlphaNumeric(peek())) { next(); }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private boolean match(char target) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != target) { return false; }

        next();
        return true;
    }

   private char next() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) { return '\0'; }
        return source.charAt(current);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') { line++; }
            next();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        next();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) next();

        if (peek() == '.' && isDigit(peekNext())) {
            next();
            while (isDigit(peek())) next();
        }

        addToken(
                TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current))
        );
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
}