	.globl	foo
	.type	foo,@function
foo:
	pushl	%ebp
	movl	%esp,%ebp
	pushl	8(%ebp)
	pushl	%ebp
	addl	$16,0(%esp)
	pushl	$3
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	jge	.L2
.L3:
	pushl	%ebp
	addl	$16,0(%esp)
	pushl	$2
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	pushl	%ebp
	addl	$16,0(%esp)
	pushl	$5
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	popl	%eax
	popl	%ecx
	cmpl	%eax,%ecx
	je	.L4
	pushl	$.L5
	call	printStr
	addl	$4,%esp
	jmp	.L3
.L4:
.L2:
.L1:
	leave
	ret
	.align	4
.L5:
	.string	"go go gadget"
	.globl	goGoGadget
	.type	goGoGadget,@function
goGoGadget:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$24,%esp
	pushl	8(%ebp)
	call	printInt
	addl	$4,%esp
	pushl	%ebp
	addl	$-24,0(%esp)
	pushl	$22
	pushl	$21
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	call	printReal
	addl	$4,%esp
	pushl	-24(%ebp)
	popl	%eax
	jmp	.L6
.L6:
	leave
	ret
	.globl	main
	.type	main,@function
main:
	pushl	%ebp
	movl	%esp,%ebp
	subl	$60,%esp
	pushl	%ebp
	addl	$-16,0(%esp)
	addl	$0,0(%esp)
	popl	%eax
	pushl	0(%eax)
	flds	.L8
	subl	$4,%esp
	fstps	0(%esp)
	pushl	$3
	call	foo
	addl	$24,%esp
	subl	$4,%esp
	fstps	0(%esp)
	flds	0(%esp)
	addl	$4,%esp
	fstps	-24(%ebp)
	pushl	-20(%ebp)
	call	goGoGadget
	addl	$4,%esp
	pushl	%eax
	popl	-44(%ebp)
	pushl	$5
	pushl	$2
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	addl	$1,0(%esp)
	pushl	%ebp
	addl	$-44,0(%esp)
	pushl	-20(%ebp)
	addl	$2,0(%esp)
	pushl	$1
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	pushl	$10
	pushl	$6
	negl	0(%esp)
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$9
	negl	0(%esp)
	negl	0(%esp)
	negl	0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	subl	%eax,0(%esp)
	popl	-20(%ebp)
	pushl	%ebp
	addl	$-60,0(%esp)
	pushl	-20(%ebp)
	pushl	$20
	negl	0(%esp)
	negl	0(%esp)
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	pushl	$2
	popl	%eax
	subl	%eax,0(%esp)
	pushl	$4
	popl	%eax
	imull	0(%esp)
	movl	%eax,0(%esp)
	popl	%eax
	addl	%eax,0(%esp)
	popl	%eax
	pushl	0(%eax)
	popl	-20(%ebp)
.L7:
	leave
	ret
	.align	4
.L8:
	.float	4.5
