dropDownBrand = $("#brand")
dropDownCategory = $("#category")

$(document).ready(function () {
    // change selections
    dropDownBrand.change(function () {
        // before append need to clear the previous content of dropdown category
        dropDownCategory.empty();
        getCategories();
    });
    // button cancel
    $("#buttonCancel").on("click", function (){
        window.location = moduleURL;
    });

    // button shipping cancel
    $("buttonShippingCancel").on("click", function () {
        window.location = moduleURL;
    });

    // Load rich text area
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    // load first time to get the category of first item brand
    getCategories();
});

// Change thumbnail when upload Main images
$("#productMainImage").change(function () {
    if (!checkImageSize(this)) return;
    showMainImageThumbnail(this);
})

// Change thumbnail when upload Extra images
// get array of input name "extraImage" to show thumbnail image
$("input[name='extraImage']").each(function (index) {
    extraImageCount++
    $(this).change(function() {
        if (!checkImageSize(this)) return;
        showExtraImageThumbnail(this, index);
    })
})

function getCategories() {
    let brandID = dropDownBrand.val()
    // Rest API : brands/{id}/categories
    let url = brandURL + "/" + brandID + "/categories";

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropDownCategory);
        })
    })
}

// check unique product name form
function checkUniqueProductName(form) {
    // let url = "[[@{/products/check_unique}]]"; khai bao ben product_form html, thymeleaf only work in html
    let productId = $("#id").val();
    let productName = $("#name").val();

    let params = {
        id: productId ,
        name: productName,
    };

    // AJAX call
    $.post(url, params, function (response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "DuplicateName") {
            ShowModalDialog("Warning", productName + " is already exist ! Please choose another name")
        } else ShowModalDialog("Warning", "Unknown response from server !");
    }).fail(function () {
        ShowModalDialog("Error", "Unable connect to server !")
    })
    // prevent submit
    return false;
}

// Check image upload size
function checkImageSize(fileInput) {
    let fileSize = fileInput.files[0].size;
    if (fileSize > 1048576) {
        fileInput.setCustomValidity("You must choose an image less than 1MB !");
        fileInput.reportValidity();
        return false;
    } else {
        fileInput.setCustomValidity("")
        return true;
    }
}

// Show Modal Dialog
function ShowModalDialog(title, message) {
    $('#titleModal').text(title)
    $('.modal-body').text(message)
    // trigger Modal
    $('#productModal').modal('show');
}

// Show Main Images Thumbnail
function showMainImageThumbnail(fileInput) {
    let file = fileInput.files[0]
    let reader = new FileReader()
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result)
    }
    reader.readAsDataURL(file);
}

// Show Extra Images Thumbnail
function showExtraImageThumbnail(fileInput, index) {
    let file = fileInput.files[0]
    let reader = new FileReader()
    reader.onload = function (e) {
        $("#extraThumbnail"+ index).attr("src", e.target.result)
    }
    reader.readAsDataURL(file);

    // after showing extra image thumbnail, add extra upload section if index > so lan chinh sua item
    if (index >= extraImageCount - 1) {
        addNextExtraImage(index + 1);
        console.log(`index: ${index} and count: ${extraImageCount}`)
    } else return
}

function addNextExtraImage(index) {
    let extraImageElement = `
        <div class="col border m-3 p-2" id="divExtraImage${index}">
             <div id="extraImageHeader${index}">                
                <label class="col-form-label">Extra Image #${index + 1}:</label>                           
             </div>             
             <div>
                <img id="extraThumbnail${index}" src="${defaultThumbnailImage}" class="img-fluid"/>
             </div>
             <input type="file" name="extraImage" accept="image/png, image/jpg" class="m-2 ms-0"
                onchange="showExtraImageThumbnail(this, ${index})"/>       
        </div>`;

    let removeExtraImageElement = `
            <a class="btn fa-solid fa-circle-xmark fa-2x float-end" 
                title="Remove this image" href="javascript:removeExtraImage(${index -1})">         
            </a>
    `;
    $("#uploadContainer").append(extraImageElement);
    // Add remove icon for all except the last item
    $("#extraImageHeader"+ (index-1)).append(removeExtraImageElement);
    // also increase count when next extra image
    extraImageCount++
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}

