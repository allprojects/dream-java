grammar Strings;
import Common;

start:	stringExpr;

stringExpr: 	stringExpr '+' stringExpr	# Concat
    |			identifier					# Id  
    |			'(' stringExpr ')'			# Parens
    ;
