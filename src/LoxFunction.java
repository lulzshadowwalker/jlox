import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Environment closure;
    private final Stmt.Function declaration;
    private final boolean isInitializer;

    LoxFunction(
            Stmt.Function declaration,
            Environment closure,
            boolean isInitializer
    ) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
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

        if (isInitializer) { closure.getAt(0, "это"); }
        return null;
    }

    public LoxFunction bind(LoxInstance instance) {
        /**
         * you define an implicit scope just outside the methods scope that define or binds to an instance
         */
        final Environment environment = new Environment(closure);
        environment.define("это", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

}


