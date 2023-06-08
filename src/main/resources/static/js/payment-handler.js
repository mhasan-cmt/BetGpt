$(function () {
    var API_KEY = $('#api-key').val();
    // Create a Stripe client.
    var stripe = Stripe(API_KEY);

    // Create an instance of Elements.
    var elements = stripe.elements();

    // Create an instance of the card Element.
    var card = elements.create('card');

    // Add an instance of the card Element into the `card-element` <div>.
    card.mount('#card-element');

    // Handle real-time validation errors from the card Element.
    card.addEventListener('change', function (event) {
        var displayError = document.getElementById('card-errors');
        if (event.error) {
            displayError.textContent = event.error.message;
        } else {
            displayError.textContent = '';
        }
    });

    // Handle form submission.
    var form = document.getElementById('card-payment-form');
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        // handle payment
        handlePayments();
    });

    //handle card submission
    function handlePayments() {
        stripe.createToken(card).then(function (result) {
            if (result.error) {
                // Inform the user if there was an error.
                let errorElement = document.getElementById('card-errors');
                errorElement.textContent = result.error.message;
                console.log(result.error.message);
            } else {
                // Send the token to your server.
                let token = result.token.id;
                let plan = $('input[name=plan]:checked').val() === 'basic' ? 'Monthly' : 'Yearly';
                let cardHolderName = $('#card-holder-name').val();
                $.post("/stripePayment/makePayment", {cardHolderName: cardHolderName, token: token, plan: plan},
                    function (data) {
                        card.clear();
                        $('#card-holder-name').val('');
                    }, 'json');
            }
        });
    }
});