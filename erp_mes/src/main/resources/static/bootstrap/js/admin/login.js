	  document.querySelectorAll(".dev-login .login").forEach((btn) => {
	    btn.addEventListener("click", function () {
	      const role = this.dataset.role;
	      let empNo = "", empPw = "";

	      switch (role) {
	        case "ADMIN":
	          empNo = "2025082801";
	          empPw = "1234";
	          break;
	        case "HR":		//인사팀 계정 추가
	          empNo = "2025082802";
	          empPw = "1234";
	          break;
			case "USER":
  	          empNo = "2025082802";
  	          empPw = "1234";
  	          break;
	      }
		  
	      document.getElementById("empId").value = empNo;
	      document.getElementById("exampleInputPassword").value = empPw;
	      document.getElementById("rememberMe").checked = false;

	      document.getElementById("loginBtn").click();
	    });
	  });
