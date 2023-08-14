app.controller("cart-ctrl", function($scope, $http){
	// QUẢN LÍ GIỎ HÀNG
	var $cart = $scope.cart = {
        items: [],
        //THÊM SẢN PHẨM VÀO GIỎ HÀNG
        add(id){ 
			//A.TÌM MẶT HÀNG TRONG GIỎ HÀNG
        	var item = this.items.find(item => item.id == id);
        	//A.TĂNG SỐ LƯỢNG SAU KHI TÌM DC VÀ SAVE VÀO LOCAL
            if(item){
                item.qty++;
                this.saveToLocalStorage();
            }
            //A.KHÔNG THÌ TẢI MẶT HÀNG TỪ SERVER THÔNG QUA API
            else{
				//REST API->PRC 
            	$http.get(`/rest/products/${id}`).then(resp => {
					//SL =1 VÀ BỎ VÀO DS ĐÃ CHỌN
            		resp.data.qty = 1;
            		this.items.push(resp.data);
            		this.saveToLocalStorage();
            	})
            }
        },
        //B.XÓA SẢN PHẨM KHỎI GIỎ HÀNG
        remove(id){ 
			//B.VỊ TRÍ SP TRONG GIỎ HÀNG THÔNG QUA ID
        	var index = this.items.findIndex(item => item.id == id);
        	//B.DÙNG SPLICE XÓA 1 PHẦN TỬ KHỎI MẢNG
            this.items.splice(index, 1);
            //B.XÓA XONG LƯU
            this.saveToLocalStorage();
        },
        //C.XÓA SẠCH CÁC MẶT HÀNG TRONG GIỎ
        clear(){ 
			//C.LÀM TRỐNG 
            this.items = []
            //C.LƯU VÀO LOCALSTORAGE
            this.saveToLocalStorage();
        },
        //TÍNH THÀNH TIỀN CỦA 1 SẢN PHẨM
        amt_of(item){ 
        	return item.price * item.qty;
        },
        //TÍNH TỔNG SỐ LƯỢNG CÁC MẶT HÀNG TRONG GIỎ
        get count(){ 
			//B.DÙNG MAP LẤY QUANTITY ĐỂ TÍNH TỔNG
            return this.items
            	.map(item => item.qty)
                .reduce((total, qty) => total += qty, 0);
        },
        //TỔNG THÀNH TIỀN CÁC MẶT HÀNG TRONG GIỎ
        get amount(){ 
			//B.DÙNG MAP LẤY QUANTITY*PRICE ĐỂ TÍNH TỔNG
            return this.items
            	.map(item => this.amt_of(item))
                .reduce((total, amt) => total += amt, 0);
        },
        //LƯU GIỎ HÀNG VÀO LOCAL STORAGE
        saveToLocalStorage(){ 
			//A.ĐỔI MẶT HÀNG SANG JSON
        	var json = JSON.stringify(angular.copy(this.items));
        	//A.SAVE JSON IN LOCAL WITH NAME "CART"
            localStorage.setItem("cart", json);
        },
        //ĐỌC GIỎ HÀNG TỪ LOCAL STORAGE
        loadFromLocalStorage(){ 
			//C.ĐỌC LẠI CART TỪ LOCAL STORAGE
            var json = localStorage.getItem("cart");
            //C.NẾU CÓ THÌ CHUYỂN SANG JSON GÀN VÔ ITEM, CÒN KO THÌ MẶT RỖNG
            this.items = json ? JSON.parse(json) : [];
        }
    }
	//LƯU HÀNG
	$cart.loadFromLocalStorage();
	
	// Đặt hàng
	$scope.order = {
			get account(){
				return {username: $auth.user.username}
			},
			//Ngày order
			createDate: new Date(),
			//Địa chỉ
			address: "",
			get orderDetails(){
				return $cart.items.map(item => {
					return {
						product:{id: item.id},
						price: item.price,
						quantity: item.qty
					}
				});
			},
			//Gửi in4 lên server
			purchase(){
				var order = angular.copy(this);
				// Thực hiện đặt hàng->rest Control
				$http.post("/rest/orders", order).then(resp => {
					alert("Đặt hàng thành công!");
					$cart.clear();
					location.href = "/order/detail/" + resp.data.id;
				}).catch(error => {
					alert("Đặt hàng lỗi!")
					console.log(error)
				})
			}
	}
})