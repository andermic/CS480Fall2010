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
	jle	.L3
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jl	.L2
.L3:
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jle	.L4
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jl	.L2
.L4:
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	8(%ebp)
	jmp	.L5
.L2:
	pushl	12(%ebp)
	pushl	8(%ebp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	12(%ebp)
.L5:
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
	pushl	$.L7
	call	printStr
	addl	$4,%esp
.L6:
	leave
	ret
	.align	4
.L7:
	.string	"Euclids GCD algorithm"
