dropDownBrand = $("#brand")
dropDownCategory = $("#category")

$(document).ready(function () {
    // change selections
    dropDownBrand.change(function () {
        // before append need to clear the previous content of dropdown category
        dropDownCategory.empty();
        getCategoriesForNewForm();
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
    getCategoriesForNewForm();

    // Remove extra image when editing
    $("a[name='removeExtraImage']").each(function (index) {
        $(this).click(function () {
            removeExtraImage(index);
        })
    })

    // Remove extra detail when editing
    $("a[name='removeExtraDetail']").each(function (index) {
        $(this).click(function () {
            removeDetailSectionByIndex(index);
        })
    })
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

function getCategoriesForNewForm() {
    let categoryId = $("#categoryId")
    let editMode = false

    // if ton tai Category ID -> set edit mode == true
    if(categoryId.length) editMode = true

    // edit mode = true -> !edit mode = false -> get all categories
    if (!editMode) getCategories();
}

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

// Add Next Detail section
function addNextDetails() {
    let allDivDetails = $("[id^='divDetail']")
    let divDetailCount = allDivDetails.length

    // extra detail index = allDivDetails length
    let extraDetailElement = `
                    <div class="d-flex flex-row align-items-center flex-wrap" id="divDetail${divDetailCount}">
                        <label class="m-3">Name: </label>
                        <input type="text" class="form-control w-25" name="detailNames" maxlength="255"/>
                        <label class="m-3">Value: </label>
                        <input type="text" class="form-control w-25" name="detailValues" maxlength="255"/>
                    </div>
    `;

    $("#detailContainer").append(extraDetailElement);

    // Get previous detail section to add the Remove button, set focus
    let previousDetailElement = allDivDetails.last();
    previousDetailElement.focus()

    // get id name of detail section
    let previousDivDetailIdName = previousDetailElement.attr("id")

    // Add remove button to div detail except last one
    let removeDetailElement = `
            <a class="btn fa-solid fa-circle-xmark fa-2x float-end" 
                title="Remove this detail" href="javascript:removeDetailByIdName('${previousDivDetailIdName}')">         
            </a>
    `;
    previousDetailElement.append(removeDetailElement)
}

function removeDetailByIdName(idName) {
    $("#" + idName).remove()
}

function removeDetailSectionByIndex(index) {
    $("#divDetail" + index).remove();
}

