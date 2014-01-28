rule=SSHSUCC: Accepted password for %user:word% from %ip:ipv4% port %port:number% %protocol:word%
annotate=SSHSUCC:+MSGID="SSHSUCC"

rule=SSHFAIL2: Failed password for %user:word% from %ip:ipv4% port %port:number% %protocol:word%
annotate=SSHFAIL2:+MSGID="SSHFAIL"

rule=SSHINVALID: Failed password for invalid user %username:word% from %src-ip:ipv4% port %src-port:number% %protocol:word%
annotate=SSHINVALID:+MSGID="SSHFAIL"
