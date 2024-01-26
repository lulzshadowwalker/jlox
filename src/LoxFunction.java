import java.util.List;

public class LoxFunction implements LoxCallable {
    private Environment closure;
    private Stmt.Function declaration;

    LoxFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        final var environment = new Environment(closure);
        final var params = declaration.parameters;

        for (int i = 0; i < params.size(); i++) {
            environment.define(
                    params.get(i).lexeme,
                    arguments.get(i)
            );
        }

        try {
            interpreter.executeBlock(
                    declaration.body,
                    environment
            );
        } catch (Return ret) {
            return ret.value;
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }}
