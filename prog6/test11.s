	.globl	gcd
	.type	gcd,@function
gcd:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jg	.L3
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jge	.L2
.L3:
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	8(%ebp)
	jmp	.L4
.L2:
	pushl	12(%ebp)
	pushl	8(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	12(%ebp)
.L4:
	pushl	8(%ebp)
	popl	%eax
	jmp	.L1
.L1:
	leave
	ret
	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	$.L6
	call	printStr
	addl	$4,%esp
.L5:
	leave
	ret
	.align	4
.L6:
	.string	"Euclids GCD algorithm"
