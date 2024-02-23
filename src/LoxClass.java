import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    final String name;

    /**
     * classes store behavior while instances store fields.
     * but technically you can still store functions as fields on instances.
     * .................................................................................................................
     * When looking up a property on an instance, if we don’t find a matching field, we look for a method with that name on the instance’s class.
     * If found, we return that. This is where the distinction between “field” and “property” becomes meaningful.
     * When accessing a property, you might get a field—a bit of state stored on the instance—or you could hit a method defined on the instance’s class.
     */
    final Map<String, LoxFunction> methods;

    final LoxClass superClass;

    LoxClass(
            String name,
            Map<String, LoxFunction> methods,
            LoxClass superClass
    ) {
        this.name = name;
        this.methods = methods;
        this.superClass = superClass;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        final LoxInstance instance = new LoxInstance(this);

        final LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    public LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superClass != null) {
            return superClass.findMethod(name);
        }

        return null;
    }
}
