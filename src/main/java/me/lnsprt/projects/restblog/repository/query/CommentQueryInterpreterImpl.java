package me.lnsprt.projects.restblog.repository.query;

import me.lnsprt.projects.restblog.model.Comment;
import me.lnsprt.projects.restblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommentQueryInterpreterImpl implements CommentQueryInterpreter {
    private CommentRepository commentdb;
    private final Pattern queryTermPattern;
    private final Pattern logicalOperatorPattern;
    private final Pattern propertyPattern;

    @Autowired
    public CommentQueryInterpreterImpl(CommentRepository commentdb) {
        this.commentdb = commentdb;
        this.queryTermPattern = Pattern.compile("(?<name>\\w+)\\s(?<operator>(eq)|(lt)|(gt)|(like))\\s(?<value>[^\\s]+)" +
                "((\\sand\\s)|(\\sor\\s)|$)");
        this.logicalOperatorPattern = Pattern.compile("\\s(?<operator>(and)|(or))\\s");
        this.propertyPattern = Pattern.compile("(post)|(user)|(text)|(date)");
    }


    @Override
    public Page<Comment> executeQuery(String query, Pageable pageRequest) throws Exception {
        Page<Comment> result = null;

        //format: name1 [eq,lt,gt,like] value1 [[and,or] name2 [eq,lt,gt,like] value2]...
        //at the moment, only AND operations and single-value properties are supported
        Matcher logOpMatcher = logicalOperatorPattern.matcher(query);
        Matcher queryTermMatcher = queryTermPattern.matcher(query);

        Integer queryTerms = 1;
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
        values[0] = (property.equals("date"))? new SimpleDateFormat("yyyy-MM-dd").parse(value): value;
        params[0] = Pageable.class;
        params[1] = values[0].getClass();
        String methodName = "findBy";
        methodName = methodName + repositoryMethodNameBuilder(property, operator, value);
        for(int q = 1; q < queryTerms; q++) {
            queryTermMatcher.find();
            property = queryTermMatcher.group("name");
            operator = queryTermMatcher.group("operator");
            value = queryTermMatcher.group("value");

            //check if query term is well-formed
            if(!parseQueryTerm(property, operator, value)) {
                throw new Exception();
            }

            values[q] = (property.equals("date"))? new SimpleDateFormat("yyyy-MM-dd").parse(value): value;
            params[q + 1] = values[q].getClass();
            methodName = methodName + "And" + repositoryMethodNameBuilder(property, operator, value);
        }

        Method repositoryMethod = CommentRepository.class.getMethod(methodName, params);
        Object[] args = new Object[queryTerms + 1];
        args[0] = pageRequest;
        for(int i = 0; i < queryTerms; i++) {
            args[i + 1] = values[i];
        }
        result = (Page<Comment>)repositoryMethod.invoke(commentdb, args);

        return result;
    }

    private Boolean parseQueryTerm(String property, String operator, String value) {
        // should check operator and value as well
        return propertyPattern.matcher(property).matches();
    }

    private String repositoryMethodNameBuilder(String property, String operator, String value) throws Exception {
        String mappedProperty = property.replaceFirst("[a-zA-Z]", property.substring(0, 1).toUpperCase());

        String mappedOperator = "";
        switch(operator) {
            case "eq":
                break;
            case "gt":
                mappedOperator = (property.equals("date"))? "After" : "GreaterThan";
                break;
            case "lt":
                mappedOperator = (property.equals("date"))? "Before" : "LessThan";
                break;
            case "like":
                mappedOperator = "Contains";

        }

        String methodName = mappedProperty + mappedOperator;

        return methodName;
    }

    @Override
    public Collection<Comment> executeQuery(String query) throws Exception {
        return null;
    }
}
