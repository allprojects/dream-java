grammar Integers;
import Common;

start:	intExpr;

intExpr:	intExpr op=(MUL|DIV) intExpr	# MulDiv
    |		intExpr op=(ADD|SUB) intExpr	# AddSub
    |		DIGIT                			# Int
    |		identifier						# Id        
    |		'(' intExpr ')'					# Parens
    ;
