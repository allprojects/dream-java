grammar Common;
    
identifier: hostId DOT observableId DOT method |
			observableId DOT method;

hostId: ID | '*';
observableId: ID;
method: ID'('')';

MUL: '*';
DIV: '/'; 
ADD: '+';
SUB: '-';
DOT: '.';
AND: '&';
OR: '|';
NOT: '!';
BOOL: 'true'|'false';

ID: [a-zA-Z] [a-zA-Z0-9]*;
DIGIT: [0-9]+;
DOUBLE: [0-9]* DOT [0-9]+;
STRING: [a-zA-Z0-9]+;

WS: [ \n\t]+ -> skip;
