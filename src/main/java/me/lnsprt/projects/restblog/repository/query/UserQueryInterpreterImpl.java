package me.lnsprt.projects.restblog.repository.query;

import me.lnsprt.projects.restblog.model.User;
import me.lnsprt.projects.restblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserQueryInterpreterImpl implements UserQueryInterpreter {
    private UserRepository userdb;
    private final Pattern queryTermPattern;
    private final Pattern logicalOperatorPattern;
    private final Pattern propertyPattern;

    @Autowired
    public UserQueryInterpreterImpl(UserRepository userdb) {
        this.userdb = userdb;
        this.queryTermPattern = Pattern.compile("(?<name>\\w+)\\s(?<operator>(eq)|(lt)|(gt)|(like))\\s(?<value>[^\\s]+)" +
                "((\\sand\\s)|(\\sor\\s)|$)");
        this.logicalOperatorPattern = Pattern.compile("\\s(?<operator>(and)|(or))\\s");
        this.propertyPattern = Pattern.compile("(name)|(nickname)|(email)|(signupDate)|(roles)|(suspended)");
    }


    @Override
    public Page<User> executeQuery(String query, Pageable pageRequest) throws Exception {
        Page<User> result;

        //format: name1 [eq,lt,gt,like] value1 [[and,or] name2 [eq,lt,gt,like] value2]...
        //at the moment, only AND logical and single-value properties are supported
        Matcher logOpMatcher = logicalOperatorPattern.matcher(query);
        Matcher queryTermMatcher = queryTermPattern.matcher(query);

        int queryTerms = 1;
        while(logOpMatcher.find()) {
            queryTerms++;
        }

        // get and invoke repository method
        queryTermMatcher.find();
        String property = queryTermMatcher.group("name");
        String operator = queryTermMatcher.group("operator");
        String value = queryTermMatcher.group("value");

        Object[] values = new Object[queryTerms];
        Class<?>[] params = new Class[queryTerms + 1];
        values[0] = propertyValueFactory(property, value);
        params[0] = Pageable.class;
        params[1] = values[0].getClass();
        StringBuilder methodName = new StringBuilder("findBy");
        methodName.append(repositoryMethodNameBuilder(property, operator, value));
        for(int q = 1; q < queryTerms; q++) {
            queryTermMatcher.find();
            property = queryTermMatcher.group("name");
            operator = queryTermMatcher.group("operator");
            value = queryTermMatcher.group("value");

            //check if query term is well-formed
            if(!parseQueryTerm(property, operator, value)) {
                throw new Exception();
            }

            values[q] = propertyValueFactory(property, value);
            params[q + 1] = values[q].getClass();
            methodName.append("And").append(repositoryMethodNameBuilder(property, operator, value));
        }

        Method repositoryMethod = UserRepository.class.getMethod(methodName.toString(), params);
        Object[] args = new Object[queryTerms + 1];
        args[0] = pageRequest;
        System.arraycopy(values, 0, args, 1, queryTerms);
        result = (Page<User>)repositoryMethod.invoke(userdb, args);

        return result;
    }

    private Boolean parseQueryTerm(String property, String operator, String value) {
        // should check operator and value as well
        return propertyPattern.matcher(property).matches();
    }

    private Object propertyValueFactory(String property, String value) throws Exception {
        Object v;

        switch(property) {
            case "signupDate":
                v = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                break;
            case "suspended":
                v = Boolean.valueOf(value);
                v.getClass();
                break;
            default:
                v = value;
        }

        return v;
    }

    private String repositoryMethodNameBuilder(String property, String operator, String value) {
        String mappedProperty = property.replaceFirst("[a-zA-Z]", property.substring(0, 1).toUpperCase());

        String mappedOperator = "";
        switch(operator) {
            case "eq":
                break;
            case "gt":
                mappedOperator = (property.equals("signupDate"))? "After" : "GreaterThan";
                break;
            case "lt":
                mappedOperator = (property.equals("signupDate"))? "Before" : "LessThan";
                break;
            case "like":
                mappedOperator = "Contains";

        }

        return mappedProperty + mappedOperator;
    }

    @Override
    public Collection<User> executeQuery(String query) throws Exception {
        return null;
    }
}
