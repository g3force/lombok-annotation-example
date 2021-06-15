package de.qaware.lombok.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.util.List;
import de.qaware.lombok.Hello;
import lombok.core.AST.Kind;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;
import static lombok.javac.handlers.JavacHandlerUtil.setGeneratedBy;


/**
 * Custom Lombok annotation handler that registers for {@link Hello} annotations.
 */
public class HelloWorldAnnotationHandler extends JavacAnnotationHandler<Hello> {
    /**
     * Handle an annotated node.
     *
     * @param annotation     the annotation instance (with parameter values)
     * @param ast            the annotation tree (the position that the compiler will point to on errors)
     * @param annotationNode the annotation node (the annotation in the source code)
     */
    @Override
    public void handle(
        final AnnotationValues<Hello> annotation,
        final JCAnnotation ast,
        final JavacNode annotationNode
    ) {
        // Get the annotated node, which should be either a method or a type (class)
        JavacNode annotatedNode = annotationNode.up();

        if (annotatedNode.getKind() == Kind.METHOD) {
            handleMethod(ast, annotatedNode);
        } else {
            annotationNode.addError("@Hello is legal only on methods and types");
        }

        // Delete annotation, if this is configured for Lombok
        deleteAnnotationIfNeccessary(annotationNode, Hello.class);
    }

    private void handleMethod(JCAnnotation ast, JavacNode methodNode) {
        JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) methodNode.get();

        // create a tree maker that is positioned at the annotation (mainly for reporting compilation issues)
        JavacTreeMaker maker = methodNode.getTreeMaker().at(ast.pos);

        // Statement that calls <tracer>.startMethod(accessScope, className, methodName, <parameter pair list>)
        JCTree.JCStatement println = setGeneratedBy(maker.Exec(maker.Apply(
            List.nil(),
            maker.Select(chainDotsString(methodNode, "java.lang.System.out"), methodNode.toName("println")),
            List.of(maker.Literal("Hello World")))),
            methodNode);

        if (method.body.stats.isEmpty() || !method.body.stats.get(0).equals(println)) {
            method.body.stats = method.body.stats.prepend(println);
            methodNode.rebuild();
        }
    }
}
