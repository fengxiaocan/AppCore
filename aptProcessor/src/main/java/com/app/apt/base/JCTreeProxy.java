package com.app.apt.base;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.LinkedHashSet;

public final class JCTreeProxy {
    protected TreeMaker treeMaker;
    protected Names names;

    public JCTreeProxy(TreeMaker treeMaker) {
        this.treeMaker = treeMaker;
    }

    public JCTreeProxy(TreeMaker treeMaker, Names names) {
        this.treeMaker = treeMaker;
        this.names = names;
    }

    public static ListBuffer<JCTree.JCStatement> getBuffer() {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
        return jcStatements;
    }

    public static boolean isEquals(Name name, String parname) {
        return parname.equals(name.toString());
    }

    public static boolean isEquals(JCTree.JCVariableDecl name, String parname) {
        return parname.equals(name.name.toString());
    }

    /**
     * 返回一个空的block
     *
     * @return
     */
    public JCTree.JCBlock emptyBlock() {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
        return treeMaker.Block(0, jcStatements.toList());
    }

    public JCTree.JCBlock block(JCTree.JCStatement... jcStatement) {
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer();
        for (JCTree.JCStatement statement : jcStatement) {
            jcStatements.append(statement);
        }
        return treeMaker.Block(0, jcStatements.toList());
    }

    /**
     * 根据字符串获取Name，（利用Names的fromString静态方法）
     *
     * @param s
     * @return
     */
    public Name getNameFromString(String s) {
        return names.fromString(s);
    }

    /**
     * 创建变量语句
     *
     * @param modifiers
     * @param name
     * @param vartype
     * @param init
     * @return
     */
    public JCTree.JCVariableDecl makeVarDef(JCTree.JCModifiers modifiers, String name,
            JCTree.JCExpression vartype, JCTree.JCExpression init)
    {
        return treeMaker.VarDef(modifiers, getNameFromString(name), //名字
                vartype, //类型
                init //初始化语句
        );
    }

    public JCTree.JCVariableDecl makeVarDef(String name, String vartype)
    {
        return treeMaker.VarDef(treeMaker.Modifiers(0), getNameFromString(name), //名字
                memberAccess(vartype), //类型
                null //初始化语句
        );
    }

    /**
     * 创建 域/方法 的多级访问, 方法的标识只能是最后一个
     *
     * @param components
     * @return
     */
    public JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = memberExpr(componentArray[0]);
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    /**
     * 创建type
     *
     * @param components
     * @return
     */
    public JCTree.JCExpression memberExpr(String components) {
        return memberExpr(getNameFromString(components));
    }

    /**
     * 创建type
     *
     * @param components
     * @return
     */
    public JCTree.JCExpression memberExpr(Name components) {
        return treeMaker.Ident(components);
    }

    public JCTree.JCPrimitiveTypeTree memberExpr(TypeTag components) {
        return treeMaker.TypeIdent(components);
    }

    /**
     * 调用方法
     *
     * @param jcExpression
     * @param components
     * @return
     */
    public JCTree.JCExpression memberAccess(JCTree.JCExpression jcExpression, String components) {
        return treeMaker.Select(jcExpression, getNameFromString(components));
    }

    /**
     * 声明变量并赋值
     */
    public JCTree.JCVariableDecl memberVariable(String name, String clazzName, Object value) {
        //JCTree.JCVariableDecl var = makeVarDef(treeMaker.Modifiers(0), "xiao", memberAccess("java.lang.String"), treeMaker.Literal("methodName"));
        return makeVarDef(treeMaker.Modifiers(0), name, memberAccess(clazzName),
                treeMaker.Literal(value));
    }

    /**
     * 方法调用（以输出语句举例）
     * @return
     */
    //    public JCTree.JCExpressionStatement mask() {
    //        //        JCTree.JCExpressionStatement printVar = treeMaker.Exec(treeMaker.Apply(
    //        //                List.of(memberAccess("java.lang.String")),//参数类型
    //        //                memberAccess("java.lang.System.out.println"),
    //        //                List.of(treeMaker.Ident(getNameFromString("xiao")))
    //        //                )
    //        //        );
    //        //生成语句：System.out.println(xiao);
    //        return treeMaker.Exec(treeMaker.Apply(List.of(memberAccess("java.lang.String")),//参数类型
    //                memberAccess("java.lang.System.out.println"),
    //                List.<JCTree.JCExpression>of(treeMaker.Ident(getNameFromString("xiao")))));
    //    }

    /**
     * 给变量赋值
     */
    public JCTree.JCExpressionStatement makeAssignment(JCTree.JCExpression lhs,
            JCTree.JCExpression rhs)
    {
        //makeAssignment(treeMaker.Ident(getNameFromString("xiao")), treeMaker.Literal("assignment test"));
        return treeMaker.Exec(treeMaker.Assign(lhs, rhs));
    }

    /**
     * 创建一个新的方法
     *
     * @param body       方法体语句
     * @param methodName
     * @param returnType
     * @param params
     */
    public JCTree.JCMethodDecl makeNewMethod(JCTree.JCBlock body, String methodName,
            JCTree.JCExpression returnType, List<JCTree.JCVariableDecl> params)
    {
        JCTree.JCMethodDecl jcMethodDecl = treeMaker.MethodDef(
                //修饰符
                treeMaker.Modifiers(Flags.PUBLIC),
                //方法名
                names.fromString(methodName),
                //返回类型
                returnType,
                //泛型参数
                List.<JCTree.JCTypeParameter>nil(),
                //参数列表
                params,
                //抛出异常列表
                List.<JCTree.JCExpression>nil(),
                //方法体
                body,
                //默认值
                null);

        return jcMethodDecl;
    }

    /**
     * 调用super的方法
     */
    public JCTree.JCExpressionStatement superMethod(Symbol.MethodSymbol symbol) {
        JCTree.JCExpression superExpression = treeMaker.Select(treeMaker.Ident(names._super),
                symbol.name);
        if (symbol.params != null && symbol.params.size() > 0) {

            LinkedHashSet<JCTree.JCExpression> set = new LinkedHashSet<>();
            for (Symbol.VarSymbol param : symbol.params) {
                set.add(treeMaker.QualIdent(param));
            }
            return treeMaker.Exec(treeMaker.Apply(List.<JCTree.JCExpression>nil(), superExpression,
                    //方法名
                    List.from(set)

            ));
        } else {
            //调用super方法
            return treeMaker.Exec(treeMaker.Apply(List.<JCTree.JCExpression>nil(), superExpression,
                    //方法名
                    List.<JCTree.JCExpression>nil()));
        }
    }
}
