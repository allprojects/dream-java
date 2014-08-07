grammar Bools;
import Common;

start:	boolExpr;

boolExpr:	NOT boolExpr					# Not
    |		boolExpr op=(AND|OR) boolExpr	# AndOr
    |		BOOL                			# Bool
    |		identifier						# Id        
    |		'(' boolExpr ')'				# Parens
    ;
