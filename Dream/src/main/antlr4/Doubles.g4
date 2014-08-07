grammar Doubles;
import Common;

start:	doubleExpr;

doubleExpr:	doubleExpr op=(MUL|DIV) doubleExpr	# MulDiv
    |		doubleExpr op=(ADD|SUB) doubleExpr	# AddSub
    |		DOUBLE                				# Double
    |		identifier							# Id        
    |		'(' doubleExpr ')'					# Parens
    ;
