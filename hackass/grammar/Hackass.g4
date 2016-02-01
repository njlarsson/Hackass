grammar Hackass;

@header {
package hackass.grammar;
}

file
: line+
;

line
: label '\n'*
| var '\n'*
| ainstr '\n'*
| cinstr '\n'*
;

label
: ID ':' '\n'*
;

var
: 'var' ID '\n'*
;

ainstr
: '@' (ID|INT|ZERONE)
;

cinstr
: (dest '=')? comp (';' jump)?
;

dest
: AMD+
;

jump
: 'JGT'
| 'JEQ'
| 'JGE'
| 'JLT'
| 'JNE'
| 'JLE'
| 'JMP'
;

comp
: ZERONE
| AMD
| '-1'
| '!D'
| '!A'
| '-D'
| '-A'
| 'D+1'
| 'A+1'
| 'D-1'
| 'A-1'
| 'D+A'
| 'D-A'
| 'A-D'
| 'D&A'
| 'D|A'
| '!M'
| '-M'
| 'M+1'
| 'M-1'
| 'D+M'
| 'D-M'
| 'M-D'
| 'D&M'
| 'D|M'
;

ZERONE: [01] ;
ID:	('a'..'z')+ ;
INT:	('0'..'9')+ ;
AMD:    [AMD] ;
WS:	[ \t\r]+ -> skip ;
COMNT:  '//' .*? '\n' -> skip ;
