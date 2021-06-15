package de.qaware.lombok.processor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCStatement;
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
        AnnotationValues<Hello> annotation,
        JCAnnotation ast,
        JavacNode annotationNode
    ) {
        // Get the annotated node, which should be either a method or a type (class)
        JavacNode annotatedNode = annotationNode.up();

        String recipient = annotation.getInstance().recipient();

        if (annotatedNode.getKind() == Kind.METHOD) {
            injectHello(ast, annotatedNode, recipient);
        } else {
            annotationNode.addError("@Hello is legal only on methods and types");
        }

        // Delete annotation, if this is configured for Lombok
        deleteAnnotationIfNeccessary(annotationNode, Hello.class);
    }

    private void injectHello(JCAnnotation ast, JavacNode methodNode, String recipient) {

        // create a tree maker that is positioned at the annotation (mainly for reporting compilation issues)
        JavacTreeMaker maker = methodNode.getTreeMaker().at(ast.pos);

        // create new println statement for the greeting
        JCStatement printlnStatement = createPrintlnStatement(maker, methodNode, recipient);

        // add new statement to the beginning of the method body
        JCMethodDecl method = (JCMethodDecl) methodNode.get();
        method.body.stats = method.body.stats.prepend(printlnStatement);
        methodNode.rebuild();
    }

    private JCTree.JCExpressionStatement createPrintlnStatement(
        JavacTreeMaker maker, JavacNode methodNode,
        String recipient
    ) {
        return setGeneratedBy(
            maker.Exec( // new expression
                maker.Apply( // new method call
                    List.nil(), // type args
                    maker.Select( // select the method
                        chainDotsString(methodNode, "java.lang.System.out"),
                        methodNode.toName("println")
                    ),
                    List.of(maker.Literal("Hello " + recipient)) // method parameters
                )
            ),
            methodNode
        );
    }
}
